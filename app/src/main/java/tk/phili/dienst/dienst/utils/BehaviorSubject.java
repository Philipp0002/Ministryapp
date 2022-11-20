package tk.phili.dienst.dienst.utils;

import java.util.ArrayList;

public class BehaviorSubject<T> {

    T value;
    private ArrayList<Subscriber> subscriberList;

    public BehaviorSubject(T value) {
        this.value = value;
        subscriberList = new ArrayList<>();
    }

    public T getValue() {
        return value;
    }

    public void next(T value) {
        this.value = value;
        subscriberList.stream().forEach(subscriber -> subscriber.onNext(value));
    }

    public void subscribe(Subscriber<T> subscriber) {
        subscriberList.add(subscriber);
        subscriber.onNext(value);
    }

    public void destroy() {
        subscriberList.stream().forEach(subscriber -> subscriber.onDestroy());
        subscriberList.clear();
    }

    public interface Subscriber<T> {
        public void onNext(T value);
        public void onDestroy();
    }

}
