package com.srobber.manager.pay.apple.dto;

import lombok.Data;

import java.util.List;

/**
 * 票据校验状态
 *  0 成功
 	21000 App Store无法读取你提供的JSON数据
	21002 收据数据不符合格式
	21003 收据无法被验证
	21004 你提供的共享密钥和账户的共享密钥不一致
	21005 收据服务器当前不可用
	21006 收据是有效的，但订阅服务已经过期。当收到这个信息时，解码后的收据信息也包含在返回内容中
	21007 收据信息是测试用（sandbox），但却被发送到产品环境中验证
	21008 收据信息是产品环境中使用，但却被发送到测试环境中验证

 * @author chensenlai
 */
@Data
public class ReceiptVerifyResultDTO {

    /**
     * receipt : {"receipt_type":"ProductionSandbox","adam_id":0,"app_item_id":0,"bundle_id":"com.xsbanruo.prajna","application_version":"58","download_id":0,"version_external_identifier":0,"receipt_creation_date":"2020-05-13 09:26:17 Etc/GMT","receipt_creation_date_ms":"1589361977000","receipt_creation_date_pst":"2020-05-13 02:26:17 America/Los_Angeles","request_date":"2020-05-13 09:33:26 Etc/GMT","request_date_ms":"1589362406406","request_date_pst":"2020-05-13 02:33:26 America/Los_Angeles","original_purchase_date":"2013-08-01 07:00:00 Etc/GMT","original_purchase_date_ms":"1375340400000","original_purchase_date_pst":"2013-08-01 00:00:00 America/Los_Angeles","original_application_version":"1.0","in_app":[{"quantity":"1","product_id":"com.Shanpin.Xsbanruo.coin.5","transaction_id":"1000000664082793","original_transaction_id":"1000000664082793","purchase_date":"2020-05-13 09:26:17 Etc/GMT","purchase_date_ms":"1589361977000","purchase_date_pst":"2020-05-13 02:26:17 America/Los_Angeles","original_purchase_date":"2020-05-13 09:26:17 Etc/GMT","original_purchase_date_ms":"1589361977000","original_purchase_date_pst":"2020-05-13 02:26:17 America/Los_Angeles","is_trial_period":"false"}]}
     * status : 0
     * environment : Sandbox
     */
    private ReceiptBean receipt;
    private int status;
    private String environment;

    @Data
    public static class ReceiptBean {
        /**
         * receipt_type : ProductionSandbox
         * adam_id : 0
         * app_item_id : 0
         * bundle_id : com.xxx.xx
         * application_version : 58
         * download_id : 0
         * version_external_identifier : 0
         * receipt_creation_date : 2020-05-13 09:26:17 Etc/GMT
         * receipt_creation_date_ms : 1589361977000
         * receipt_creation_date_pst : 2020-05-13 02:26:17 America/Los_Angeles
         * request_date : 2020-05-13 09:33:26 Etc/GMT
         * request_date_ms : 1589362406406
         * request_date_pst : 2020-05-13 02:33:26 America/Los_Angeles
         * original_purchase_date : 2013-08-01 07:00:00 Etc/GMT
         * original_purchase_date_ms : 1375340400000
         * original_purchase_date_pst : 2013-08-01 00:00:00 America/Los_Angeles
         * original_application_version : 1.0
         * in_app : [{"quantity":"1","product_id":"com.Shanpin.Xsbanruo.coin.5","transaction_id":"1000000664082793","original_transaction_id":"1000000664082793","purchase_date":"2020-05-13 09:26:17 Etc/GMT","purchase_date_ms":"1589361977000","purchase_date_pst":"2020-05-13 02:26:17 America/Los_Angeles","original_purchase_date":"2020-05-13 09:26:17 Etc/GMT","original_purchase_date_ms":"1589361977000","original_purchase_date_pst":"2020-05-13 02:26:17 America/Los_Angeles","is_trial_period":"false"}]
         */

        private String receipt_type;
        private String adam_id;
        private String app_item_id;
        private String bundle_id;
        private String application_version;
        private String download_id;
        private String version_external_identifier;
        private String receipt_creation_date;
        private String receipt_creation_date_ms;
        private String receipt_creation_date_pst;
        private String request_date;
        private String request_date_ms;
        private String request_date_pst;
        private String original_purchase_date;
        private String original_purchase_date_ms;
        private String original_purchase_date_pst;
        private String original_application_version;
        private List<InAppBean> in_app;

        @Data
        public static class InAppBean {
            /**
             * quantity : 1
             * product_id : com.Shanpin.Xsbanruo.coin.5
             * transaction_id : 1000000664082793
             * original_transaction_id : 1000000664082793
             * purchase_date : 2020-05-13 09:26:17 Etc/GMT
             * purchase_date_ms : 1589361977000
             * purchase_date_pst : 2020-05-13 02:26:17 America/Los_Angeles
             * original_purchase_date : 2020-05-13 09:26:17 Etc/GMT
             * original_purchase_date_ms : 1589361977000
             * original_purchase_date_pst : 2020-05-13 02:26:17 America/Los_Angeles
             * is_trial_period : false
             */

            private String quantity;
            private String product_id;
            private String transaction_id;
            private String original_transaction_id;
            private String purchase_date;
            private String purchase_date_ms;
            private String purchase_date_pst;
            private String original_purchase_date;
            private String original_purchase_date_ms;
            private String original_purchase_date_pst;
            private String is_trial_period;
        }
    }
}