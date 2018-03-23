package com.hotger.recipes;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity
public class Fake {
    @PrimaryKey
    @NonNull
    private String en;
    private String ru;
    private String measure;

    public Fake() {
        en = "";
        ru = "";
        measure = "";
    }

    public Fake(String en, String ru, String measure) {
        this.en = en;
        this.ru = ru;
        this.measure = measure;
    }

    //region Getters and setters
    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }
    //endregion


//    public static void getFromMetadata(String metadata) {
//        String data = "ingredient";
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                String str = "";
//                try {
//                    URL url = new URL(YummlyAPI.BASE_URL + "v1/api/metadata/" + data + "?" + YummlyAPI.BASE);
//                    BufferedReader reader = null;
//                    reader = new BufferedReader(new InputStreamReader(url.openStream()));
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        str += line;
//                    }
//                    reader.close();
//                    str = str.replace("set_metadata('" + data + "', ", "");
//                    str = str.substring(0, str.length() - 2);// + "}";
//                    JSONArray jsonArray = new JSONArray(str);
//                    JSONArray newArray = new JSONArray();
//                    for(int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
//                        JSONObject newObj = new JSONObject();
//                        newObj.put("en", jsonObject.getString("searchValue"));
//                        newObj.put("ru", "");
//                        newObj.put("measure", "gr");
//                        newArray.put(newObj);
//                    }
//
//                    Log.d("TAG", "text");
////                    Gson gson = new Gson();
////                    ArrayList<Product> products = gson.fromJson(str, new TypeToken<ArrayList<Product>>() {
////                    }.getType());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        Thread t = new Thread(runnable);
//        t.start();
//    }
}
