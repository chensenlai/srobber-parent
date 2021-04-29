/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.spring.web.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.SwaggerExt;
import io.swagger.models.parameters.AbstractSerializableParameter;
import io.swagger.models.parameters.Parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JsonSerializer {
  private ObjectMapper objectMapper = new ObjectMapper();

  public JsonSerializer(List<JacksonModuleRegistrar> modules) {
    for (JacksonModuleRegistrar each : modules) {
      each.maybeRegisterModule(objectMapper);
    }
  }

  public Json toJson(Object toSerialize) {
    //Swagger
    if(toSerialize instanceof Swagger) {
      Swagger swagger = (Swagger)toSerialize;
      Map<String, Path> paths = swagger.getPaths();
      fillParameterTypeWithFormat(paths);
    }
    //SwaggerBootstrapUI
    if(toSerialize instanceof SwaggerExt) {
      SwaggerExt swaggerExt = (SwaggerExt)toSerialize;
      Map<String, Path> paths = swaggerExt.getPaths();
      fillParameterTypeWithFormat(paths);
    }
    try {
      return new Json(objectMapper.writeValueAsString(toSerialize));
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Could not write JSON", e);
    }
  }

  /**
   * 接口参数如果有format类型,则拼接在type
   * 客户端需要看到integer是32位还是64位
   * @param paths
   */
  private static void fillParameterTypeWithFormat(Map<String, Path> paths) {
    if(paths == null) {
      return;
    }
    for(String pathKey : paths.keySet()) {
      Path path = paths.get(pathKey);
      List<Operation> operations = path.getOperations();
      if(operations == null) {
        continue;
      }
      for(Operation operation : operations) {
        List<Parameter> parameters = operation.getParameters();
        for(Parameter parameter : parameters) {
          if(parameter instanceof AbstractSerializableParameter) {
            AbstractSerializableParameter aParameter = (AbstractSerializableParameter)parameter;
            if(aParameter.getFormat()!=null && !aParameter.getFormat().isEmpty()) {
              if(!(aParameter.getType().contains("(")
                && aParameter.getType().contains(")"))) {
                aParameter.setType(aParameter.getType()+"("+aParameter.getFormat()+")");
              }
            }
          }
        }

        parameters = groupByParamType(parameters);
        operation.setParameters(parameters);
      }

    }

  }

  /**
   * 按参数分组放在一起
   * @param parameters
   * @return
   */
  private static List<Parameter> groupByParamType(List<Parameter> parameters) {
    if(parameters == null) {
      return null;
    }
    List<Parameter> headerParameters = new ArrayList<>();
    List<Parameter> pathParameters = new ArrayList<>();
    List<Parameter> queryParameters = new ArrayList<>();
    List<Parameter> bodyParameters = new ArrayList<>();
    List<Parameter> restParameters = new ArrayList<>();
    for(Parameter parameter : parameters) {
      if(Objects.equals(parameter.getIn(), "header")) {
        headerParameters.add(parameter);
      } else if(Objects.equals(parameter.getIn(), "path")) {
        pathParameters.add(parameter);
      } else if(Objects.equals(parameter.getIn(), "query")) {
        queryParameters.add(parameter);
      } else if(Objects.equals(parameter.getIn(), "body")) {
        bodyParameters.add(parameter);
      } else {
        restParameters.add(parameter);
      }
    }

    List<Parameter> groupByParameters = new ArrayList<>(parameters.size());
    groupByParameters.addAll(headerParameters);
    groupByParameters.addAll(pathParameters);
    groupByParameters.addAll(queryParameters);
    groupByParameters.addAll(bodyParameters);
    groupByParameters.addAll(restParameters);

    return groupByParameters;
  }
}
