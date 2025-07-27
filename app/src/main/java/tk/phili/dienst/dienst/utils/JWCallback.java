package tk.phili.dienst.dienst.utils;

import java.util.List;

public interface JWCallback<T> {
    public void onSuccess(T result);
    public void onError(Exception e);
}
