package jadex.gradle.exceptions;

import com.android.build.api.transform.TransformException;

/**
 * Created by kalinowski on 16.08.16.
 */
public class BDIEnhanceException extends TransformException {

    private final String model;

    public BDIEnhanceException(Throwable throwable, String model) {
        super(throwable);
        this.model = model;
    }

    @Override
    public String toString() {
        return "Error loading model: " + model + ". \n" + super.toString();
    }
}
