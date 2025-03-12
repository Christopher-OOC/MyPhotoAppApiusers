package com.javalord.MyPhotoAppApiUsers.shared;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {

        switch (response.status()) {
            case 400 :
                //return new RuntimeException("Bad Request");
                break;
            case 404 : {
                if (methodKey.contains("getAlbums")) {
                    return new ResponseStatusException(HttpStatusCode.valueOf(response.status()), "User albums are not found!");
                }
            }
            default : {
                return new Exception(response.reason());
            }
        }

        return null;
    }
}
