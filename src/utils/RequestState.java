package utils;

import java.io.Serializable;

public interface RequestState extends Serializable{
    String getStatusName();
    void approve(Request request);
    void reject(Request request);
}