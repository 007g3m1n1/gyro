package beam.aws.cloudfront;

import beam.core.diff.Diffable;
import software.amazon.awssdk.services.cloudfront.model.CustomErrorResponse;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CloudFrontCustomErrorResponse extends Diffable {

    private long ttl;
    private Integer errorCode;
    private String responseCode;
    private String responsePagePath;
    private Boolean customizeErrorResponse;

    private static final Set<Integer> ERROR_CODE_LIST = new HashSet<>(Arrays.asList(400,403,404,405,414,416,500,501,502,503,504));

    public CloudFrontCustomErrorResponse() {
    }

    public CloudFrontCustomErrorResponse(CustomErrorResponse errorResponse) {
        setTtl(errorResponse.errorCachingMinTTL());
        setErrorCode(errorResponse.errorCode());
        setResponseCode(errorResponse.responseCode());
        setResponsePagePath(errorResponse.responsePagePath());
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getResponseCode() {
        if (responseCode == null) {
            return "";
        }

        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponsePagePath() {
        if (responsePagePath == null) {
            responsePagePath = "";
        }

        return responsePagePath;
    }

    public void setResponsePagePath(String responsePagePath) {
        this.responsePagePath = responsePagePath;
    }

    public Boolean getCustomizeErrorResponse() {
        if (customizeErrorResponse == null) {
            customizeErrorResponse = false;
        }

        return customizeErrorResponse;
    }

    public void setCustomizeErrorResponse(Boolean customizeErrorResponse) {
        this.customizeErrorResponse = customizeErrorResponse;
    }

    public CustomErrorResponse toCustomErrorResponse() {
        return CustomErrorResponse.builder()
            .errorCachingMinTTL(getTtl())
            .errorCode(getErrorCode())
            .responseCode(getResponseCode())
            .responsePagePath(getResponsePagePath()).build();
    }

    @Override
    public String primaryKey() {
        return getErrorCode() != null ? getErrorCode().toString() : "";
    }

    @Override
    public String toDisplayString() {
        return String.format("error response - code: %d, ttl: %d", getErrorCode(), getTtl());
    }
}