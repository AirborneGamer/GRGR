package airbornegamer.com.grgr4;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class LocalRepDataHelper {

    Context mContext;
    boolean localRepActivityHeader = false;

    public LocalRepDataHelper(Context mContext) {
        this.mContext = mContext;
    }

    BitmapDrawable matchPictureToRepInfo(String repID) {

        AssetManager assets = mContext.getResources().getAssets();
        try {
            InputStream buffer = new BufferedInputStream((assets.open("repid" + repID + ".jpg")));

            Bitmap bitmap = BitmapFactory.decodeStream(buffer);
            if (bitmap == null) {
                return new BitmapDrawable(mContext.getResources(), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.unknownrep));
            }
            //GOOD!
            return new BitmapDrawable(mContext.getResources(), bitmap);

        } catch (Exception ex1) {
            //todo handle this
            try {
                return new BitmapDrawable(mContext.getResources(), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.unknownrep));
            } catch (Exception ex2) {
                //todo handle this
                return null;
            }
        }
    }

    public String getStateFullNameFromAbbreivation(String stateAbbreviation) {
        String[] knownStates = mContext.getResources().getStringArray(R.array.KnowStates);
        for (int i = 0; i < knownStates.length; i++) {
            String[] StatePair = knownStates[i].split(",");
            //StatePair[0] is full state name (Nebraska) ; StatePair[1] is state abbreviation (NE).
            if (stateAbbreviation.equals(StatePair[1]))
                return StatePair[0];
        }
        return "UnknownState";
    }

    public String getStateAbbreviationAndFullName(String fullStateName) {
        String[] knownStates = mContext.getResources().getStringArray(R.array.KnowStates);
        for (int i = 0; i < knownStates.length; i++) {
            String[] StatePair = knownStates[i].split(",");
            //StatePair[0] is full state name (Nebraska) ; StatePair[1] is state abbreviation (NE).
            if (fullStateName.equals(StatePair[0]))
                return StatePair[0] + "," + StatePair[1];
        }
        return "UnknownState";
    }

    public Boolean stateIsKnown(String currentState) {
        String[] knownStates = mContext.getResources().getStringArray(R.array.KnowStates);
        for (int i = 0; i < knownStates.length; i++) {
            String[] StatePair = knownStates[i].split(",");

            //StatePair[0] is full state name (Nebraska) ; StatePair[1] is state abbreviation (nebraska_outline).
            if (StatePair[0].equals(currentState) || StatePair[1].equals(currentState))
                return true;
        }
        return false;
    }

    public RepDetailInfo buildSelectedRepInfo(String repID) {

        String[] allRepData = mContext.getResources().getStringArray(R.array.RepData);
        for (int i = 0; i < allRepData.length; i++) {
            String[] RepArray = allRepData[i].split(",");
            String id = RepArray[0].substring(3);

            if (repID.equals(id)) {
                //String repID = RepArray[0].substring(3);
                String repState = RepArray[1].substring(7);
                String repParty = RepArray[2].substring(7);
                String repTitle = RepArray[3].substring(7);
                String repFirstName = RepArray[4].substring(11);
                String repLastName = RepArray[5].substring(10);
                String repAddress = RepArray[6].substring(9);
                String repPhone = RepArray[7].substring(7);
                String repWebiste = RepArray[8].substring(9);
                String repTwitter = RepArray[9].substring(9);
                String repYouTube = RepArray[10].substring(9);
                String repEmail = RepArray[11].substring(14);

                return new RepDetailInfo(repID, repState, repParty, repTitle, repFirstName, repLastName, false, repAddress, repPhone, repWebiste, repTwitter, repYouTube, repEmail);
            }
        }
        return null;
    }

    public ArrayList<RepDetailInfo> buildStateSpecificData(String currentState) {

        ArrayList<RepDetailInfo> allRepInfo = new ArrayList<>();

        String[] allRepData = mContext.getResources().getStringArray(R.array.RepData);
        for (int i = 0; i < allRepData.length; i++) {
            String[] RepArray = allRepData[i].split(",");
            String state = RepArray[1].substring(7);
            if (currentState.equals(state)) {

                String repID = RepArray[0].substring(3);
                String repState = RepArray[1].substring(7);
                String repParty = RepArray[2].substring(7);
                String repTitle = RepArray[3].substring(7);
                String repFirstName = RepArray[4].substring(11);
                String repLastName = RepArray[5].substring(10);
                String repAddress = RepArray[6].substring(9);
                String repPhone = RepArray[7].substring(7);
                String repWebiste = RepArray[8].substring(9);
                String repTwitter = RepArray[9].substring(9);
                String repYouTube = RepArray[10].substring(9);
                String repEmail = RepArray[11].substring(14);

                RepDetailInfo currentRepInfo = new RepDetailInfo(repID, repState, repParty, repTitle, repFirstName, repLastName, false, repAddress, repPhone, repWebiste, repTwitter, repYouTube, repEmail);

                allRepInfo.add(currentRepInfo);
            }
        }
        Collections.sort(allRepInfo, new sortStateSpecificData());
        return allRepInfo;
    }

    //Sorts list of representatives.
    public class sortStateSpecificData implements Comparator<RepDetailInfo> {
        @Override
        public int compare(RepDetailInfo o1, RepDetailInfo o2) {
            return o1.lastName.compareTo(o2.lastName);
        }
    }

    View myHeader;
    public void buildCustomStateHeader(View header, String stateFullName) {

        //Set State Flag
        myHeader = header;
        localRepActivityHeader = true;
        try {
            new getCurrentStateFlagForHeader().execute(stateFullName).get();
        } catch (Exception ex) {
            //todo handle this
        }

        //Set State Outline
        try {
            new getCurrentStateOutline().execute(stateFullName).get();
        } catch (Exception ex) {
            //todo handle this
        }

        //Set State Name
        TextView txtCurrentState = (TextView) header.findViewById(R.id.txtCurrentState);
        txtCurrentState.setText(stateFullName);
    }

    View repStateView;
    View repStateOutlineView;
    public void getDetailedStateFlagAndOutline(String fullStateName, View repStateView, View repStateOutlineView) {
        try {
            this.repStateView = repStateView;
            this.repStateOutlineView = repStateOutlineView;
            new getCurrentStateOutline().execute(fullStateName).get();
            new getCurrentStateFlagForHeader().execute(fullStateName).get();
        } catch (Exception ex) {

        }
    }
/*    public BitmapDrawable getCurrentStateFlagForDetailedRepInfo(String state) {

        state = state.toLowerCase();
        if (state.contains(" "))
            state = state.replace(" ", "_");

        AssetManager assets = mContext.getApplicationContext().getResources().getAssets();

        try {
            InputStream buffer = new BufferedInputStream((assets.open(state + ".jpg")));
            Bitmap bitmap = BitmapFactory.decodeStream(buffer);
            BitmapDrawable bmDrawable = new BitmapDrawable(mContext.getApplicationContext().getResources(), bitmap);

            if (bmDrawable == null) {
                return null;
            }

            return bmDrawable;

        } catch (Exception ex) {
            return null;
        }
    }*/

    private class getCurrentStateFlagForHeader extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... params) {

            String imageStateName = params[0].toLowerCase();
            if (imageStateName.contains(" "))
                imageStateName = imageStateName.replace(" ", "_");

            AssetManager assets = mContext.getApplicationContext().getResources().getAssets();
            try {
                InputStream buffer = new BufferedInputStream((assets.open(imageStateName + ".jpg")));
                Bitmap bitmap = BitmapFactory.decodeStream(buffer);
                BitmapDrawable bmDrawable = new BitmapDrawable(mContext.getApplicationContext().getResources(), bitmap);
                return bmDrawable.getBitmap();
            } catch (Exception ex) {
                //todo handle this
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap currentFlag) {
            asyncCallbackSetStateFlag(currentFlag);
        }
    }

    public void asyncCallbackSetStateFlag(Bitmap stateFlag) {
        if (localRepActivityHeader) {
            ImageView currentStateFlag = (ImageView) myHeader.findViewById(R.id.imgCurrentState);
            currentStateFlag.setImageBitmap(stateFlag);
        } else {
            ImageView currentStateFlag = (ImageView) repStateView.findViewById(R.id.imgRepSelectedState);
            currentStateFlag.setImageBitmap(stateFlag);
        }
    }


    public class getCurrentStateOutline extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... params) {
            String imageStateName = params[0].toLowerCase();
            if (imageStateName.contains(" ")) {
                imageStateName = imageStateName.replace(" ", "_");
            }

            imageStateName = imageStateName + "_outline";

            AssetManager assets = mContext.getApplicationContext().getResources().getAssets();
            try {
                InputStream buffer = new BufferedInputStream((assets.open(imageStateName + ".jpg")));
                Bitmap bitmap = BitmapFactory.decodeStream(buffer);
                BitmapDrawable bmDrawable = new BitmapDrawable(mContext.getApplicationContext().getResources(), bitmap);
                return bmDrawable.getBitmap();
            } catch (Exception ex) {
                //todo handle this
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap currentFlag) {
            asyncCallbackSetStateOutline(currentFlag);
        }
    }

    public void asyncCallbackSetStateOutline(Bitmap stateOutline) {
        if (localRepActivityHeader) {
            ImageView currentStateOutline = (ImageView) myHeader.findViewById(R.id.imgCurrentStateOutline);
            currentStateOutline.setImageBitmap(stateOutline);
        } else {
            ImageView currentStateOutline = (ImageView) repStateOutlineView.findViewById(R.id.imgRepSelectedStateOutline);
            currentStateOutline.setImageBitmap(stateOutline);
        }
    }
}



