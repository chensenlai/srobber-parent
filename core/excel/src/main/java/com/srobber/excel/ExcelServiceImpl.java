package com.srobber.excel;

import com.srobber.common.result.BatchResult;
import com.srobber.common.result.ServiceResult;
import com.srobber.common.util.BeanUtil;
import com.srobber.common.util.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 提供java bean和excel互转能力
 *
 * @author chensenlai
 */
@Slf4j
public final class ExcelServiceImpl implements ExcelService {

	private static final String TITLE_RESULT = "#校验结果#";
	private static final String TITLE_MSG = "#异常原因#";

	private static final String SUCCESS = "成功";
	private static final String FAIL = "失败";

	@Override
	public <T> BatchResult<T> excelToBeanList(File upload, Class<T> clazz,
                                                     Map<String, Object> contextData) {

		Excel excel = clazz.getAnnotation(Excel.class);
		if(excel == null) {
			return BatchResult.fail("类"+clazz+"需加上@Excel注解");
		}

		Workbook uploadWorkbook = null;
		try {
			uploadWorkbook = WorkbookFactory.create(new FileInputStream(upload));
			FormulaEvaluator formulaEvaluator = uploadWorkbook.getCreationHelper().createFormulaEvaluator();
			Sheet sheet = uploadWorkbook.getSheetAt(excel.sheet());
			//解析excel和bean的映射关系
			ServiceResult<ExcelMeta<T>> excelMetaResult = doCheckAndBuildExcelMeta(ExcelAction.ExcelToBeanList, sheet, clazz);
			if(excelMetaResult.isFail()) {
				return BatchResult.fail(excelMetaResult.getMsg());
			}
			ExcelMeta<T> excelMeta = excelMetaResult.getData();

			//解析excel和bean字段的映射关系
			ServiceResult<List<ExcelFieldMeta<?>>> excelFieldMetaResult = doCheckAndBuildExcelFieldMeta(ExcelAction.ExcelToBeanList, sheet, clazz);
			if(excelFieldMetaResult.isFail()) {
				return BatchResult.fail(excelFieldMetaResult.getMsg());
			}
			List<ExcelFieldMeta<?>> excelFieldMetaList = excelFieldMetaResult.getData();

			//解析excel数据行,转化成javabean
			int firstDataRowNum = excelMeta.getDataRowNum();
			int lastDataRowNum = sheet.getLastRowNum();
			int total = 0; 
			int success = 0;
			List<T> dataList = new ArrayList<>();
			for (int i=firstDataRowNum; i<=lastDataRowNum; i++) {
				Row dataRow = sheet.getRow(i);
				if(dataRow == null) {
					continue;
				}
				String result = SUCCESS;
				String msg = "";

				boolean hasData = false;
				T bean = clazz.newInstance();
				for(ExcelFieldMeta<?> excelFieldMeta : excelFieldMetaList) {
					Field field = excelFieldMeta.getField();
					String fieldName = field.getName();
					Class<?> fieldType = field.getType();
					//单元值获取
					String cellValue = null;
					try {
						Cell cell = dataRow.getCell(excelFieldMeta.getCol());
						cellValue = doGetCellValue(cell, formulaEvaluator);
					} catch (Exception e) {
						log.error("cell value error, name:{} type:{} row:{} col:{}", fieldName, fieldType, i, excelFieldMeta.getCol(), e);
						result = FAIL;
						msg = "值获取失败："+e.getMessage();
					}
					//单元格值转化
					Object fieldValue = null;
					try{
						CellConverter<?> converter = excelFieldMeta.getConverter();
						fieldValue = converter.cellToField(cellValue, contextData);
					} catch(Exception e) {
						log.error("field value error, name:{} type:{} row:{} col:{}", fieldName, fieldType, i, excelFieldMeta.getCol(), e);
						result = FAIL;
						msg = "值转换失败："+e.getMessage();
					}
					//设置字段值到bean对象
					if(fieldValue == null 
							|| fieldValue.equals("-")
							|| StringUtils.isBlank(fieldValue.toString())) {
						continue;
					}
					try {
						doSetBeanFieldValue(bean, field, fieldValue);
						hasData = true;
					} catch(Exception e) {
						log.error("field value error, name:{} type:{} value:{}", fieldName, fieldType, fieldValue, e);
						result = FAIL;
						msg = "值设置失败："+fieldName+"-"+fieldValue;
					}
				}
				if(hasData) {
					total++;
					if(result.equals(SUCCESS)) {
						BeanValidator<T> validator = excelMeta.getValidator();
						String validateResult = validator.validate(bean, contextData);
						if(StringUtil.isBlank(validateResult)) {
							success++;
							dataList.add(bean);
						} else {
							result = FAIL;
							msg = validateResult;
						}
					}
					
					//处理记录结果列和数据列
					if(excelMeta.getResultColNum()>-1) {
						Cell dataReseultCell = dataRow.getCell(excelMeta.getResultColNum());
						dataReseultCell.setCellValue(result);
					}
					if(excelMeta.getMsgColNum()>-1) {
						Cell dataMsgCell = dataRow.getCell(excelMeta.getMsgColNum());
						dataMsgCell.setCellValue(msg);
					}
				}
	 		}
			uploadWorkbook.write(new FileOutputStream(upload));
			return BatchResult.ok(total, success, dataList);
		} catch (Exception e) {
			log.error("excelToBeanList error", e);
			return BatchResult.fail("未知异常："+e.getMessage());
		} finally {
			if(uploadWorkbook != null) {
				try {
					uploadWorkbook.close();
				} catch (IOException e) {}
			}
		}
	}

	@Override
	public <T>ServiceResult<byte[]> beanListToExcel(List<T> beanList, Map<String, Object> contextData) {
		if(beanList==null || beanList.size()<1) {
			return ServiceResult.fail("没有可导出数据");
		}
		@SuppressWarnings("unchecked")
		Class<T> clazz = (Class<T>)beanList.get(0).getClass();
		Excel excel = clazz.getAnnotation(Excel.class);
		if(excel == null) {
			return ServiceResult.fail("类"+clazz+"需加上@Excel注解");
		}

		//解析excel和bean的映射关系
		ServiceResult<ExcelMeta<T>> excelMetaResult = doCheckAndBuildExcelMeta(ExcelAction.BeanListToExcel, null, clazz);
		if(excelMetaResult.isFail()) {
			return ServiceResult.fail(excelMetaResult.getMsg());
		}
		ExcelMeta<T> excelMeta = excelMetaResult.getData();

		//解析excel和bean字段的映射关系
		ServiceResult<List<ExcelFieldMeta<?>>> excelFieldMetaResult = doCheckAndBuildExcelFieldMeta(ExcelAction.BeanListToExcel, null, clazz);
		if(excelFieldMetaResult.isFail()) {
			return ServiceResult.fail(excelFieldMetaResult.getMsg());
		}
		List<ExcelFieldMeta<?>> excelFieldMetaList = excelFieldMetaResult.getData();

		Workbook dataWorkbook = null;
		try {
			//dataWB = new HSSFworkbook();  //2003版本
			dataWorkbook = new XSSFWorkbook();	//2007版本
			Sheet sheet = dataWorkbook.createSheet("导出列表");
			//创建表头行
			Row titleRow = sheet.createRow(excelMeta.getTitleRowNum());
			CellStyle titleStyle = doCreateTitleStyle(dataWorkbook);
			for(ExcelFieldMeta<?> excelFieldMeta : excelFieldMetaList) {
				int col = excelFieldMeta.getCol();
				Cell cell = titleRow.createCell(col);
				cell.setCellStyle(titleStyle);
				cell.setCellValue(fieldName(excelFieldMeta.getField()));
			}
			//创建数据行
			int rowNum = excelMeta.getDataRowNum();
			CellStyle style = doCreateStyle(dataWorkbook);
			for(T bean : beanList) {
				Row row = sheet.createRow(rowNum);
				for(ExcelFieldMeta<?> excelFieldMeta : excelFieldMetaList) {
					int col = excelFieldMeta.getCol();
					Field field = excelFieldMeta.getField();
					Object fieldValue = doGetBeanFieldValue(bean, field);
					CellConverter converter = excelFieldMeta.getConverter();
					Object cellValue = converter.fieldToCell(fieldValue, contextData);
					if(cellValue != null) {
						Cell cell = row.createCell(col);
						cell.setCellStyle(style);
						cell.setCellValue(cellValue.toString());
					}
				}
				rowNum++;
			}
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			dataWorkbook.write(bos);
			return ServiceResult.ok(bos.toByteArray());
		} catch(Exception e) {
			log.error("beanListToExcel error", e);
			return ServiceResult.fail("未知异常："+e.getMessage());
		}  finally {
			if(dataWorkbook != null) {
				try {
					dataWorkbook.close();
				} catch (IOException e) {}
			}
		}
	}

	@Override
	public <T>ServiceResult<byte[]> beanToExcelTemplate(File template, T bean, Map<String, Object> contextData) {
		if(bean == null) {
			return ServiceResult.fail("没有可导出数据");
		}
		@SuppressWarnings("unchecked")
		Class<T> clazz = (Class<T>)bean.getClass();
		Excel excel = clazz.getAnnotation(Excel.class);
		if(excel == null) {
			return ServiceResult.fail("类"+clazz+"需加上@Excel注解");
		}

		Workbook templateWorkbook = null;
		try {
			templateWorkbook = WorkbookFactory.create(template);
			Sheet sheet = templateWorkbook.getSheetAt(excel.sheet());
			//解析excel和bean的映射关系
			ServiceResult<ExcelMeta<T>> excelMetaResult = doCheckAndBuildExcelMeta(ExcelAction.BeanToExcelTemplate, sheet, clazz);
			if(excelMetaResult.isFail()) {
				return ServiceResult.fail(excelMetaResult.getMsg());
			}
			ExcelMeta<T> excelMeta = excelMetaResult.getData();

			//解析excel和bean字段的映射关系
			ServiceResult<List<ExcelFieldMeta<?>>> excelFieldMetaResult = doCheckAndBuildExcelFieldMeta(ExcelAction.BeanToExcelTemplate, sheet, clazz);
			if(excelFieldMetaResult.isFail()) {
				return ServiceResult.fail(excelFieldMetaResult.getMsg());
			}
			List<ExcelFieldMeta<?>> excelFieldMetaList = excelFieldMetaResult.getData();

			CellStyle style = templateWorkbook.createCellStyle();
			for(ExcelFieldMeta<?> excelFieldMeta : excelFieldMetaList) {
				int rowNum = excelFieldMeta.getRow();
				int colNum = excelFieldMeta.getCol();
				Field field = excelFieldMeta.getField();
				Object fieldValue = doGetBeanFieldValue(bean, field);
				CellConverter converter = excelFieldMeta.getConverter();
				Object cellValue = converter.fieldToCell(fieldValue, contextData);
				if(cellValue != null) {
					Row row = sheet.getRow(rowNum);
					if(row == null) {
						row = sheet.createRow(rowNum);
					}
					Cell cell = row.getCell(colNum);
					if(cell == null) {
						cell = row.createCell(colNum);
					}
					cell.setCellStyle(style);
					cell.setCellValue(cellValue.toString());
				}
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			templateWorkbook.write(bos);
			return ServiceResult.ok(bos.toByteArray());
		} catch(Exception e) {
			log.error("beanToExcelTemple error", e);
			return ServiceResult.fail("未知异常："+e.getMessage());
		}  finally {
			if(templateWorkbook != null) {
				try {
					templateWorkbook.close();
				} catch (IOException e) {}
			}
		}
	}

	@Data
	private static class ExcelMeta<T> {
		private int sheetNum;
		private int titleRowNum;
		private int dataRowNum;
		private BeanValidator<T> validator;
		private int resultColNum;
		private int msgColNum;

		public static <T>ExcelMeta build(Class<T> clazz) {
			ExcelMeta<T> excelMeta = new ExcelMeta<>();
			Excel excel = clazz.getAnnotation(Excel.class);
			excelMeta.setSheetNum(excel.sheet());
			excelMeta.setTitleRowNum(excel.titleRow());
			excelMeta.setDataRowNum(excel.dataRow());
			try {
				excelMeta.setValidator(excel.validator().newInstance());
			} catch (Exception e) {
				log.error("excel meta error", e);
			}
			excelMeta.setResultColNum(excel.resultCol());
			excelMeta.setMsgColNum(excel.msgCol());
			return excelMeta;
		}
	}

	@Data
	private static class ExcelFieldMeta<T> {
		private int row;
		private int col;
		private Field field;
		private CellConverter<T> converter;

		public static ExcelFieldMeta<?> build(int row, int col, Class<?> clazz, Field field) {
			ExcelFieldMeta<?> excelFieldMeta = new ExcelFieldMeta<>();
			excelFieldMeta.setRow(row);
			excelFieldMeta.setCol(col);
			excelFieldMeta.setField(field);
			try {
				ExcelField excelField = field.getAnnotation(ExcelField.class);
				excelFieldMeta.setConverter(excelField.converter().newInstance());
			} catch (Exception e) {
				log.error("excel field meta error", e);
			}
			return excelFieldMeta;
		}
	}

	static enum ExcelAction {
		ExcelToBeanList,
		BeanListToExcel,
		BeanToExcelTemplate,
	}
	/**
	 * 获取excel元数据
	 */
	public static <T>ServiceResult<ExcelMeta<T>> doCheckAndBuildExcelMeta(ExcelAction excelAction, Sheet sheet, Class<T> clazz) {
		Excel excel = clazz.getAnnotation(Excel.class);
		int titleRowNum = excel.titleRow();
		int dataRowNum = excel.dataRow();
		int resultColNum = excel.resultCol();
		int msgColNum = excel.msgCol();
		if(excelAction == ExcelAction.ExcelToBeanList) {
			//1 检查指定的标题行和数据行是否超过范围
			int firstRowNum = sheet.getFirstRowNum();
			int lastRowNum = sheet.getLastRowNum();
			if(firstRowNum > titleRowNum || lastRowNum < dataRowNum) {
				ServiceResult.fail("导入excel错误，标题行在"+titleRowNum+",数据行在"+dataRowNum);
			}
			//重置记录校验结果列
			ServiceResult<Void> sr = doCheckAndResetCol(sheet, titleRowNum, dataRowNum, resultColNum, TITLE_RESULT);
			if(sr.isFail()) {
				return ServiceResult.fail(sr.getMsg());
			}
			//重置记录校验原因列
			sr = doCheckAndResetCol(sheet, titleRowNum, dataRowNum, msgColNum, TITLE_MSG);
			if(sr.isFail()) {
				return ServiceResult.fail(sr.getMsg());
			}
		}
		ExcelMeta<T> excelMeta = ExcelMeta.build(clazz);
		return ServiceResult.ok(excelMeta);
	}

	/**
	 * 校验和重置校验结果列和异常原因列
	 */
	private static ServiceResult<Void> doCheckAndResetCol(Sheet sheet, int titleRowNum, int dataRowNum,
														  int colNum, String value) {
		if(colNum < 0) {
			return ServiceResult.ok();
		}
		//检查和设置标题列
		Row titleRow = sheet.getRow(titleRowNum);
		Cell cell = titleRow.getCell(colNum);
		if(cell == null) {
			cell = titleRow.createCell(colNum);
		}
		if(StringUtil.isNotBlank(cell.getStringCellValue())
				&& !cell.getStringCellValue().equals(value)) {
			return ServiceResult.fail("导入excel错误，"+value+"列位置需在"+colNum);
		}
		cell.setCellValue(value);

		//清空数据对应校验结果列
		for (int i=dataRowNum; i<=sheet.getLastRowNum(); i++) {
			Row dataRow = sheet.getRow(i);
			if(dataRow == null) {
				continue;
			}
			Cell dataCell = dataRow.getCell(colNum);
			if(dataCell == null) {
				dataCell = dataRow.createCell(colNum);
			}
			dataCell.setCellValue("");
		}
		return ServiceResult.ok();
	}

	/**
	 * 获取excel列和field元数据
	 */
	public static <T>ServiceResult<List<ExcelFieldMeta<?>>> doCheckAndBuildExcelFieldMeta(ExcelAction excelAction, Sheet sheet, Class<T> clazz) {
		List<ExcelFieldMeta<?>> excelFieldMetaList = new ArrayList<>();
		Field[] fields = clazz.getDeclaredFields();
		if(excelAction == ExcelAction.BeanListToExcel) {
			Excel excel = clazz.getAnnotation(Excel.class);
			int i = excel.titleRow();
			int j = 0;
			for(Field field : fields) {
				ExcelField excelField = field.getAnnotation(ExcelField.class);
				if (excelField == null) {
					continue;
				}
				ExcelFieldMeta<?> excelFieldMeta = ExcelFieldMeta.build(i, j, clazz, field);
				excelFieldMetaList.add(excelFieldMeta);
				j++;
			}
		} else if(excelAction==ExcelAction.ExcelToBeanList || excelAction==ExcelAction.BeanToExcelTemplate){
			//有指定模板,则在遍历模板的单元格,找到对应匹配单元
			for(Field field : fields) {
				ExcelField excelField = field.getAnnotation(ExcelField.class);
				if(excelField == null) {
					continue;
				}
				String fieldName = fieldName(field);
				boolean match = false;
				int firstRowNum = sheet.getFirstRowNum();
				int lastRowNum = sheet.getLastRowNum();
				for(int i=firstRowNum; i<=lastRowNum && !match; i++) {
					Row row = sheet.getRow(i);
					if(row == null) {
						continue;
					}
					int firstCellNum = row.getFirstCellNum();
					int lastCellNum = row.getLastCellNum();
					for(int j=firstCellNum; j<=lastCellNum; j++) {
						Cell cell = row.getCell(j);
						if(cell == null) {
							continue;
						}
						String cellValue = cell.getStringCellValue();
						if(StringUtil.isBlank(cellValue)) {
							continue;
						}
						if(cellValue.equals(fieldName)) {
							ExcelFieldMeta<?> excelFieldMeta = ExcelFieldMeta.build(i, j, clazz, field);
							excelFieldMetaList.add(excelFieldMeta);
							match = true;
							break;
						}
					}
				}
				if(!match) {
					return ServiceResult.fail(fieldName+"在excel里找不到映射");
				}
			}
		}
		return ServiceResult.ok(excelFieldMetaList);
	}

	private static String fieldName(Field field) {
		ExcelField excelField = field.getAnnotation(ExcelField.class);
		if(StringUtil.isBlank(excelField.name())) {
			return field.getName();
		}
		return excelField.name();
	}

	private static void doSetBeanFieldValue(Object bean, Field field, Object fieldValue) {
		try {
			Class<?> fieldType = field.getType();
			if(fieldType.isAssignableFrom(String.class)) {
				fieldValue = fieldValue.toString();
			} if(fieldType.isAssignableFrom(Integer.class) || fieldType == int.class) {
				fieldValue = Integer.parseInt(fieldValue.toString());
			} else if(fieldType.isAssignableFrom(Long.class) || fieldType == long.class) {
				fieldValue = Long.parseLong(fieldValue.toString());
			} else if(fieldType.isAssignableFrom(Float.class)|| fieldType == float.class) {
				fieldValue = Float.parseFloat(fieldValue.toString());
			} else if(fieldType.isAssignableFrom(Double.class) || fieldType == double.class) {
				fieldValue = Double.parseDouble(fieldValue.toString());
			}
			Method setterMethod = bean.getClass().getMethod(BeanUtil.getSetterMethodName(field.getName()));
			if(setterMethod != null) {
				setterMethod.invoke(bean, fieldValue);
			} else {
				boolean accessible = field.isAccessible();
				field.setAccessible(true);
				field.set(bean, fieldValue);
				field.setAccessible(accessible);
			}
		} catch (Exception e) {
			log.error("bean value set error", e);
		}
	}

	/**
	 * 从bean去某个字段的值
	 * @param bean
	 * @param field
	 * @return
	 */
	private static Object doGetBeanFieldValue(Object bean, Field field) {
		Object fieldValue = null;
		try {
			Method getterMethod = bean.getClass().getMethod(BeanUtil.getGetterMethodName(field.getName(), field.getType()));
			if(getterMethod != null) {
				fieldValue = getterMethod.invoke(bean);
			} else {
				boolean accessible = field.isAccessible();
				field.setAccessible(true);
				fieldValue = field.get(bean);
				field.setAccessible(accessible);
			}
		} catch (Exception e) {
			log.error("bean value get error", e);
			fieldValue = null;
		}
		return fieldValue;
	}

	/**
	 * 获取cell对应值,支持formula
	 */
	private static String doGetCellValue(Cell cell, FormulaEvaluator formulaEvaluator) {
		if (cell == null) {
			return "";
		}
		String cellvalue = null;
		switch (cell.getCellType()) {
			case NUMERIC: {
				short format = cell.getCellStyle().getDataFormat();
				if(format == 14 || format == 31 || format == 57 || format == 58){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					double value = cell.getNumericCellValue();
					Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);
					cellvalue = sdf.format(date);
				} else if (HSSFDateUtil.isCellDateFormatted(cell)) {
					Date date = cell.getDateCellValue();
					DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
					cellvalue= formater.format(date);
				} else {
					cellvalue = NumberToTextConverter.toText(cell.getNumericCellValue());
				}
				break;
			}
			case STRING:
				cellvalue = cell.getStringCellValue().trim();
				break;
			case BOOLEAN:
				cellvalue = Boolean.toString(cell.getBooleanCellValue());
				break;
			case BLANK:
				cellvalue = "";
				break;
			case FORMULA:
				cellvalue = doGetCellValue(formulaEvaluator.evaluateInCell(cell), formulaEvaluator);
				break;
			default:{
				cellvalue = " ";
			}
		}
		return cellvalue;
	}

	/**
	 * 创建标题cell格式
	 */
	private static CellStyle doCreateTitleStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setBorderBottom(BorderStyle.NONE);
		style.setBorderRight(BorderStyle.NONE);
		
		Font font = workbook.createFont();
		font.setFontName("宋体");
		font.setBold(true);
        font.setFontHeightInPoints((short)10);
        style.setFont(font);
        return style;
	}

	/**
	 * 创建数据cell格式
	 */
	private static CellStyle doCreateStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setBorderBottom(BorderStyle.NONE);
		style.setBorderRight(BorderStyle.NONE);
		
		Font font = workbook.createFont();
		font.setFontName("宋体");
        font.setFontHeightInPoints((short)10);
        style.setFont(font);
        return style;
	}
}
