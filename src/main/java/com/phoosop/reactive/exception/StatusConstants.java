package com.phoosop.reactive.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class StatusConstants {

    @Getter
    @AllArgsConstructor
    public enum HttpConstants {

        SUCCESS("0", "Success"),
        USER_ACTION_IS_INVALID("TEMP31327", "user_action is invalid"),
        USER_ACTION_IS_REQUIRED("TEMP31328", "user_action is required"),
        USER_TYPE_IS_INVALID("TEMP31327", "user_type is invalid"),
        USER_TYPE_IS_REQUIRED("TEMP31328", "user_type is required"),
        FAILED_TO_CONVERT_VALUE_TO_ENUM("31329", "Failed to convert value to enum"),
        TMN_ID_IS_REQUIRED("TEMP31330", "tmn_id is required"),
        DATE_IS_INVALID("TEMP31331", "date is invalid"),
        VALUE_IS_INVALID("TEMP31332", "value is invalid"),
        FAILED_TO_CALCULATE_KYC_LEVEL("TEMP31333", "Failed to calculate kyc level"),
        FAILED_TO_GET_WALLET_SIZE("TEMP31334", "Failed to get wallet size"),
        FAIL_TO_UPDATE_ACCOUNT_DETAILS("TEMP31335", "Fail to upload account details"),
        USER_NOT_FOUND("TEMP31336", "User not found"),
        CARD_ID_IS_REQUIRED("TEMP31337", "card_id is required"),
        FIRST_NAME_IS_REQUIRED("TEMP31338", "first_name is required"),
        SECOND_NAME_IS_REQUIRED("TEMP31339", "second_name is required"),
        TYPE_IS_REQUIRED("TEMP31340", "type is required"),
        STATUS_IS_REQUIRED("TEMP31341", "status is required"),
        LEVEL_IS_REQUIRED("TEMP31342", "level is required"),
        MOBILE_NUMBER_IS_REQUIRED("TEMP31343", "mobile_number is required"),
        MOBILE_BRAND_IS_REQUIRED("TEMP31344", "mobile_brand is required"),
        TYPE_IS_INVALID("TEMP31345", "type is invalid"),
        STATUS_IS_INVALID("TEMP31346", "status is invalid"),
        LEVEL_IS_INVALID("TEMP31347", "level is invalid"),
        DATE_OF_BIRTH_IS_REQUIRED("TEMP31348", "date_of_birth is required"),
        DATE_OF_BIRTH_IS_INVALID("TEMP31349", "date_of_birth is invalid"),
        AGE_IS_REQUIRED("TEMP31350", "age is required"),
        AGE_IS_INVALID("TEMP31351", "age is invalid"),
        USER_ID_IS_REQUIRED("TEMP31352", "user_id is required"),
        PASSED_VERIFICATION_TYPE_IS_REQUIRED("TEMP31353", "passed_verification_type is required"),

        NOT_FOUND("TEMP31997", "Not found"),
        BAD_REQUEST("TEMP31998", "Bad request"),
        INTERNAL_SERVER_ERROR("TEMP31999", "Internal server error");

        private final String code;
        private final String desc;

    }

}
