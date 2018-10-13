package tk.phili.dienst.dienst;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.util.Date;

public class Person
{
    private static final int BIBLE_STUDY_TYPE = 2;
    private static final int BOOKMARKED_TYPE = 8;
    private static final int MAGAZINE_ROUTE_TYPE = 4;
    private static final int RETURN_VISIT_TYPE = 1;
    private static final int VISIT_DAY = 2;
    private static final int VISIT_EVENING = 4;
    private static final int VISIT_FRIDAY = 256;
    private static final int VISIT_MONDAY = 16;
    private static final int VISIT_MORNING = 1;
    private static final int VISIT_SATURDAY = 512;
    private static final int VISIT_SUNDAY = 1024;
    private static final int VISIT_THURSDAY = 128;
    private static final int VISIT_TUESDAY = 32;
    private static final int VISIT_WEDNESDAY = 64;
    private static final int VISIT_WEEKEND = 8;
    public static final int eChild = 2;
    public static final int eCouple = 3;
    public static final int eGentleman = 1;
    public static final int eLady = 0;
    public static final int eUnknown = 4;
    private static String[] items;
    private static Date mOneWeekAgo;

    public String address;
    public int Age;
    public String apartment;
    public int bestVisitTime;
    public String city;
    public String country;
    public Date date;
    public String email;
    public String generalInfo;
    public String homePhone;
    public long id;
    public boolean isHidden;
    public boolean isInterested;
    public boolean isPinnedToFront;
    public String languages;
    public Date lastTriedTime;
    public double latitude;
    public double longitude;
    public String motherTongue;
    public String name;
    public int personType;
    public String phone;
    public String postalCode;
    public int priority;
    public String studyPublication;
    public String suburb;
    public Date timeForRV;
    public int Tp;
    public String videoPhone;
    public String whereToContinue;





    /*public void loadFromFileRaw(JsonParser paramJsonParser, int paramInt)
            throws Exception
    {
        for (;;)
        {
            try
            {
                personType = 0;
                if (isManaged()) {
                    realmGet$visitInfoList().deleteAllFromRealm();
                } else {
                    realmGet$visitInfoList().clear();
                }
                paramJsonParser.nextToken();
                d2 = 0.0D;
                d1 = d2;
                if (paramJsonParser.nextToken() != JsonToken.END_OBJECT)
                {
                    localObject = paramJsonParser.getCurrentName();
                    paramJsonParser.nextToken();
                    if ("Inf".equals(localObject))
                    {
                        if (paramJsonParser.nextToken() == JsonToken.END_OBJECT) {
                            continue;
                        }
                        localObject = new VisitInfo();
                        ((VisitInfo)localObject).loadFromFileRaw(paramJsonParser);
                    }
                }
            }
            catch (Exception paramJsonParser)
            {
                double d2;
                double d1;
                Object localObject;
                LogToSD.write("Person.loadFromFileRaw 1", paramJsonParser.getMessage());
                throw paramJsonParser;
            }
            try
            {
                realmGet$visitInfoList().add(localObject);
            }
            catch (Exception localException)
            {
                continue;
            }
            LogToSD.write("dummy", "");
            continue;
            if ("Tp".equals(localObject))
            {
                realmSet$type(getPersonType(paramJsonParser.getText()));
            }
            else if ("TpI".equals(localObject))
            {
                realmSet$type(paramJsonParser.getIntValue());
            }
            else if ("Add".equals(localObject))
            {
                realmSet$address(paramJsonParser.getText());
            }
            else if ("Hidden".equals(localObject))
            {
                realmSet$isHidden(paramJsonParser.getBooleanValue());
            }
            else if ("Pinned".equals(localObject))
            {
                realmSet$isPinnedToFront(paramJsonParser.getBooleanValue());
            }
            else if ("Intd".equals(localObject))
            {
                realmSet$isInterested(paramJsonParser.getBooleanValue());
            }
            else if ("LstTry".equals(localObject))
            {
                realmSet$lastTriedTime(new Date(paramJsonParser.getLongValue()));
            }
            else if ("Sub".equals(localObject))
            {
                realmSet$suburb(paramJsonParser.getText().trim());
            }
            else if ("N".equals(localObject))
            {
                realmSet$name(paramJsonParser.getText());
            }
            else if ("Age".equals(localObject))
            {
                realmSet$age(paramJsonParser.getIntValue());
            }
            else if ("Prio".equals(localObject))
            {
                realmSet$priority(paramJsonParser.getIntValue());
            }
            else if ("Fav".equals(localObject))
            {
                if (paramJsonParser.getBooleanValue() == true) {
                    realmSet$personType(realmGet$personType() | 0x8);
                }
            }
            else if ("Eml".equals(localObject))
            {
                realmSet$email(paramJsonParser.getText());
            }
            else if ("Zip".equals(localObject))
            {
                realmSet$postalCode(paramJsonParser.getText());
            }
            else if ("Apt".equals(localObject))
            {
                realmSet$apartment(paramJsonParser.getText());
            }
            else if ("Ph".equals(localObject))
            {
                realmSet$phone(paramJsonParser.getText());
            }
            else if ("Phh".equals(localObject))
            {
                realmSet$homePhone(paramJsonParser.getText());
            }
            else if ("VP".equals(localObject))
            {
                realmSet$videoPhone(paramJsonParser.getText());
            }
            else if ("C".equals(localObject))
            {
                realmSet$country(paramJsonParser.getText());
            }
            else if ("Lan".equals(localObject))
            {
                realmSet$languages(paramJsonParser.getText());
            }
            else if ("Ton".equals(localObject))
            {
                realmSet$motherTongue(paramJsonParser.getText());
            }
            else if ("Gen".equals(localObject))
            {
                realmSet$generalInfo(paramJsonParser.getText());
            }
            else if ("SPub".equals(localObject))
            {
                realmSet$studyPublication(paramJsonParser.getText());
            }
            else if ("SCnt".equals(localObject))
            {
                realmSet$whereToContinue(paramJsonParser.getText());
            }
            else if ("TRV".equals(localObject))
            {
                realmSet$timeForRV(new Date(paramJsonParser.getLongValue()));
            }
            else if ("City".equals(localObject))
            {
                realmSet$city(paramJsonParser.getText().trim());
            }
            else if ("Date".equals(localObject))
            {
                realmSet$date(new Date(paramJsonParser.getLongValue()));
            }
            else if ("ID".equals(localObject))
            {
                if (paramInt <= 24) {
                    realmSet$id(paramJsonParser.getIntValue());
                } else {
                    realmSet$id(paramJsonParser.getLongValue());
                }
            }
            else if ("Lat".equals(localObject))
            {
                d2 = paramJsonParser.getIntValue() / 1000000.0D;
            }
            else if ("Lon".equals(localObject))
            {
                d1 = paramJsonParser.getIntValue() / 1000000.0D;
            }
            else if ("Type".equals(localObject))
            {
                realmSet$personType(paramJsonParser.getIntValue());
            }
            else if ("Mags".equals(localObject))
            {
                if (paramJsonParser.nextToken() != JsonToken.END_ARRAY) {
                    realmGet$magazineList().add(Integer.valueOf(paramJsonParser.getIntValue()));
                }
            }
            else if ("Tags".equals(localObject))
            {
                if (realmGet$customTags() != null) {
                    realmSet$customTags(new RealmList());
                }
                if (paramJsonParser.nextToken() != JsonToken.END_ARRAY) {
                    realmGet$customTags().add(paramJsonParser.getText());
                }
            }
            else
            {
                if (!"VTm".equals(localObject)) {
                    continue;
                }
                realmSet$bestVisitTime(paramJsonParser.getIntValue());
            }
        }
        paramJsonParser = new StringBuilder();
        paramJsonParser.append("Unrecognized field '");
        paramJsonParser.append((String)localObject);
        paramJsonParser.append("'!");
        throw new IllegalStateException(paramJsonParser.toString());
        if ((d2 != 0.0D) && (d1 != 0.0D))
        {
            realmSet$latitude(d2);
            realmSet$longitude(d1);
        }
        if ((paramInt < 15) && (isBibleStudy()))
        {
            paramJsonParser = realmGet$visitInfoList().iterator();
            while (paramJsonParser.hasNext()) {
                ((VisitInfo)paramJsonParser.next()).setCountStudy(true);
            }
        }
    }*/

}

