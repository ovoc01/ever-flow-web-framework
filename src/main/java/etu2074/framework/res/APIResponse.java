package etu2074.framework.res;

import java.util.HashMap;

import etu2074.framework.res.status.HttpStatus;

public class APIResponse {
    HashMap<String, Object> data = new HashMap<String, Object>();

    HttpStatus status = HttpStatus.OK;
    public void addData(String key, Object value) {
        data.put(key, value);
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setStatus(int status) {
        this.status = HttpStatus.getHttpStatus(status);
    }
}
