package airbornegamer.com.grgr4;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

 class LocalRepData {

    Context mContext;

    public LocalRepData(Context mContext) {
        this.mContext = mContext;
    }

    public String getCurrentUsersState() {

        LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        try {
            Geocoder gcd = new Geocoder(mContext.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            return addresses.get(0).getAdminArea();
        } catch (Exception ex) {
            return null;
        }

    }

    public Boolean physicalStateIsKnown(String currentState) {

        String[] knownStates = mContext.getResources().getStringArray(R.array.KnowStates);
        for (int i = 0; i < knownStates.length; i++) {
            String[] StatePair = knownStates[i].split(",");

            //StatePair[0] is full state name (Nebraska) ; StatePair[1] is state abbreviation (NE).
            if (StatePair[0].equals(currentState) || StatePair[1].equals(currentState)) {
                return true;
            }
        }
        return false;
    }

    public String buildCustomAPIURL(String currentState){
        String firstPartOfURL = "https://www.govtrack.us/api/v2/role?state=";
        String lastPartOfURL = "&current=true";

        String[] knownStates = mContext.getResources().getStringArray(R.array.KnowStates);
        for(int i = 0; i < knownStates.length; i++){
            String[] StatePair = knownStates[i].split(",");
            if (currentState.equals(StatePair[0])){
                return firstPartOfURL + StatePair[1] + lastPartOfURL;
            }
        }
        return "";
    }

    public ArrayList<String> filterRepDataForUser(JSONObject allRepData){
            try{

                JSONArray allRepsResults = allRepData.getJSONArray("objects");
                ArrayList<String> aList = new ArrayList<String>();

                for (int i = 0; i < allRepsResults.length(); i++) {
                    JSONObject currentItem = allRepsResults.getJSONObject(i);
//                String phNumber = currentItem.getString("phone");
//                String webSite = currentItem.getString("website");

                    JSONObject person = currentItem.getJSONObject("person");

                    String name = person.getString("name");
                    String nickname = person.getString("nickname"); //check if empty

                    if(nickname.isEmpty()){
                        aList.add(name);
                    }else{
                        aList.add(name + "aka " + nickname);
                    }
                }

                return aList;

            }catch (Exception ex){
                String myEx = ex.toString();
                //trace out bad stuff
            }
            return null;
    }
}

