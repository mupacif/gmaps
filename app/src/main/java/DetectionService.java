import android.app.IntentService;
import android.content.Intent;
import android.nfc.Tag;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.GeofencingEvent;

/**
 * Created by student on 15-03-17.
 */

public class DetectionService extends IntentService {
    public final static String Tag = "detected_is";

    public DetectionService()
    {

        super(Tag);

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
