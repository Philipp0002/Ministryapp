package tk.phili.dienst.dienst;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.marcoscg.licenser.Library;
import com.marcoscg.licenser.License;
import com.marcoscg.licenser.LicenserDialog;
import com.yarolegovich.mp.MaterialEditTextPreference;
import com.yarolegovich.mp.MaterialStandardPreference;
import com.yarolegovich.mp.MaterialSwitchPreference;
import com.yarolegovich.mp.io.MaterialPreferences;
import com.yarolegovich.mp.io.StorageModule;
import com.yarolegovich.mp.io.UserInputModule;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.os.Bundle;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;


public class Settings extends MainActivity {

    public SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public AlertDialog.Builder builder;

    String jwlangs = "{\"languages\":[{\"code\":\"ABK\",\"locale\":\"ab\",\"vernacular\":\"аԥсуа\",\"name\":\"Abkhaz\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"AF\",\"locale\":\"af\",\"vernacular\":\"Afrikaans\",\"name\":\"Afrikaans\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"AL\",\"locale\":\"sq\",\"vernacular\":\"shqip\",\"name\":\"Albanian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"ALU\",\"locale\":\"alz\",\"vernacular\":\"Alur\",\"name\":\"Alur\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"ASL\",\"locale\":\"ase\",\"vernacular\":\"American Sign Language\",\"name\":\"American Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"AM\",\"locale\":\"am\",\"vernacular\":\"አማርኛ\",\"name\":\"Amharic\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"LAS\",\"locale\":\"sgn_ao\",\"vernacular\":\"Língua angolana de sinais\",\"name\":\"Angolan Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"A\",\"locale\":\"ar\",\"vernacular\":\"العربية\",\"name\":\"Arabic\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":true},{\"code\":\"LSA\",\"locale\":\"aed\",\"vernacular\":\"lengua de señas argentina\",\"name\":\"Argentinean Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"REA\",\"locale\":\"hy\",\"vernacular\":\"Հայերեն\",\"name\":\"Armenian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"AKN\",\"locale\":\"djk\",\"vernacular\":\"Okanisitongo\",\"name\":\"Aukan\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"AUS\",\"locale\":\"asf\",\"vernacular\":\"Auslan (Australian Sign Language)\",\"name\":\"Australian Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"AP\",\"locale\":\"ay\",\"vernacular\":\"Aymara\",\"name\":\"Aymara\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"AJR\",\"locale\":\"az\",\"vernacular\":\"Azərbaycan\",\"name\":\"Azerbaijani\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"BAK\",\"locale\":\"ba\",\"vernacular\":\"башҡорт\",\"name\":\"Bashkir\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"BQ\",\"locale\":\"eu\",\"vernacular\":\"Euskara\",\"name\":\"Basque\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"BS\",\"locale\":\"bas\",\"vernacular\":\"Basaa (Kamerun)\",\"name\":\"Bassa (Cameroon)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"AK\",\"locale\":\"btx\",\"vernacular\":\"Batak (Karo)\",\"name\":\"Batak (Karo)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"BT\",\"locale\":\"bbc\",\"vernacular\":\"Batak (Toba)\",\"name\":\"Batak (Toba)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"BZK\",\"locale\":\"bzj\",\"vernacular\":\"Bileez Kriol\",\"name\":\"Belize Kriol\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"BE\",\"locale\":\"bn\",\"vernacular\":\"বাংলা\",\"name\":\"Bengali\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"IK\",\"locale\":\"bhw\",\"vernacular\":\"Biak\",\"name\":\"Biak\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"BI\",\"locale\":\"bcl\",\"vernacular\":\"Bicol\",\"name\":\"Bicol\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"LM\",\"locale\":\"bi\",\"vernacular\":\"Bislama\",\"name\":\"Bislama\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"BVL\",\"locale\":\"bvl\",\"vernacular\":\"lengua de señas boliviana\",\"name\":\"Bolivian Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"BO\",\"locale\":\"bum\",\"vernacular\":\"Bulu\",\"name\":\"Boulou\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"LSB\",\"locale\":\"bzs\",\"vernacular\":\"Língua brasileira de sinais\",\"name\":\"Brazilian Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"BSL\",\"locale\":\"bfi\",\"vernacular\":\"British Sign Language\",\"name\":\"British Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"BL\",\"locale\":\"bg\",\"vernacular\":\"български\",\"name\":\"Bulgarian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"CB\",\"locale\":\"km\",\"vernacular\":\"ខ្មែរ\",\"name\":\"Cambodian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"AN\",\"locale\":\"cat\",\"vernacular\":\"català\",\"name\":\"Catalan\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"CV\",\"locale\":\"ceb\",\"vernacular\":\"Cebuano\",\"name\":\"Cebuano\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"CN\",\"locale\":\"ny\",\"vernacular\":\"Chichewa\",\"name\":\"Chichewa\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"SCH\",\"locale\":\"csg\",\"vernacular\":\"lengua de señas chilena\",\"name\":\"Chilean Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"CNS\",\"locale\":\"yue_hans\",\"vernacular\":\"中文简体（广东话）\",\"name\":\"Chinese Cantonese (Simplified)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"CHC\",\"locale\":\"yue_hant\",\"vernacular\":\"中文繁體（廣東話）\",\"name\":\"Chinese Cantonese (Traditional)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"CHS\",\"locale\":\"cmn_hans\",\"vernacular\":\"中文简体（普通话）\",\"name\":\"Chinese Mandarin (Simplified)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"CH\",\"locale\":\"cmn_hant\",\"vernacular\":\"中文繁體（國語）\",\"name\":\"Chinese Mandarin (Traditional)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"CSL\",\"locale\":\"csl\",\"vernacular\":\"中国手语\",\"name\":\"Chinese Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"CG\",\"locale\":\"toi\",\"vernacular\":\"Chitonga\",\"name\":\"Chitonga\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"CT\",\"locale\":\"tog\",\"vernacular\":\"Chitonga (Malawi)\",\"name\":\"Chitonga (Malawi)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"TB\",\"locale\":\"tum\",\"vernacular\":\"Chitumbuka\",\"name\":\"Chitumbuka\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"CK\",\"locale\":\"cjk\",\"vernacular\":\"Chokwe\",\"name\":\"Chokwe\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"CHL\",\"locale\":\"ctu\",\"vernacular\":\"ch'ol\",\"name\":\"Chol\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"CU\",\"locale\":\"cv\",\"vernacular\":\"чӑвашла\",\"name\":\"Chuvash\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"CW\",\"locale\":\"bem\",\"vernacular\":\"Cibemba\",\"name\":\"Cibemba\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"CIN\",\"locale\":\"nya\",\"vernacular\":\"Cinyanja\",\"name\":\"Cinyanja\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"LSC\",\"locale\":\"csn\",\"vernacular\":\"lengua de señas colombiana\",\"name\":\"Colombian Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"C\",\"locale\":\"hr\",\"vernacular\":\"hrvatski\",\"name\":\"Croatian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"CBS\",\"locale\":\"csf\",\"vernacular\":\"lenguaje de señas cubano\",\"name\":\"Cuban Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"B\",\"locale\":\"cs\",\"vernacular\":\"čeština\",\"name\":\"Czech\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"CSE\",\"locale\":\"cse\",\"vernacular\":\"český znakový jazyk\",\"name\":\"Czech Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"DMR\",\"locale\":\"naq_x_dmr\",\"vernacular\":\"Damara\",\"name\":\"Damara\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"DG\",\"locale\":\"ada\",\"vernacular\":\"Dangme\",\"name\":\"Dangme\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"D\",\"locale\":\"da\",\"vernacular\":\"Dansk\",\"name\":\"Danish\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"DA\",\"locale\":\"dua\",\"vernacular\":\"Douala\",\"name\":\"Douala\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"O\",\"locale\":\"nl\",\"vernacular\":\"Nederlands\",\"name\":\"Dutch\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"SEC\",\"locale\":\"ecs\",\"vernacular\":\"lengua de señas ecuatoriana\",\"name\":\"Ecuadorian Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"EF\",\"locale\":\"efi\",\"vernacular\":\"Efịk\",\"name\":\"Efik\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"E\",\"locale\":\"en\",\"vernacular\":\"English\",\"name\":\"English\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"ST\",\"locale\":\"et\",\"vernacular\":\"eesti\",\"name\":\"Estonian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"STD\",\"locale\":\"eso\",\"vernacular\":\"eesti viipekeel\",\"name\":\"Estonian Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"ESL\",\"locale\":\"eth\",\"vernacular\":\"የኢትዮጵያ ምልክት ቋንቋ\",\"name\":\"Ethiopian Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"EW\",\"locale\":\"ee\",\"vernacular\":\"Eʋegbe\",\"name\":\"Ewe\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"EWN\",\"locale\":\"ewo\",\"vernacular\":\"Ewondo\",\"name\":\"Ewondo\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"FGN\",\"locale\":\"fan\",\"vernacular\":\"Fang\",\"name\":\"Fang\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"FN\",\"locale\":\"fj\",\"vernacular\":\"vakaViti\",\"name\":\"Fijian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"FSL\",\"locale\":\"psp\",\"vernacular\":\"Filipino Sign Language\",\"name\":\"Filipino Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"FI\",\"locale\":\"fi\",\"vernacular\":\"suomi\",\"name\":\"Finnish\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"FID\",\"locale\":\"fse\",\"vernacular\":\"suomalainen viittomakieli\",\"name\":\"Finnish Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"F\",\"locale\":\"fr\",\"vernacular\":\"Français\",\"name\":\"French\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"LSF\",\"locale\":\"fsl\",\"vernacular\":\"Langue des signes française\",\"name\":\"French Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"GA\",\"locale\":\"gaa\",\"vernacular\":\"Ga\",\"name\":\"Ga\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"GLC\",\"locale\":\"gl\",\"vernacular\":\"Galego\",\"name\":\"Galician\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"GRF\",\"locale\":\"cab\",\"vernacular\":\"Garifuna\",\"name\":\"Garifuna\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"GE\",\"locale\":\"ka\",\"vernacular\":\"ქართული\",\"name\":\"Georgian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"X\",\"locale\":\"de\",\"vernacular\":\"Deutsch\",\"name\":\"German\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"DGS\",\"locale\":\"gsg\",\"vernacular\":\"Deutsche Gebärdensprache\",\"name\":\"German Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"GHM\",\"locale\":\"bbj\",\"vernacular\":\"Bandjoun-Baham\",\"name\":\"Ghomálá’\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"G\",\"locale\":\"el\",\"vernacular\":\"Ελληνική\",\"name\":\"Greek\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"GSL\",\"locale\":\"gss\",\"vernacular\":\"Ελληνική Νοηματική Γλώσσα\",\"name\":\"Greek Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"GI\",\"locale\":\"gug\",\"vernacular\":\"guarani\",\"name\":\"Guarani\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"EG\",\"locale\":\"guw\",\"vernacular\":\"Gungbe\",\"name\":\"Gun\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"CR\",\"locale\":\"ht\",\"vernacular\":\"Kreyòl ayisyen\",\"name\":\"Haitian Creole\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"HA\",\"locale\":\"ha\",\"vernacular\":\"Hausa\",\"name\":\"Hausa\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"Q\",\"locale\":\"he\",\"vernacular\":\"עברית\",\"name\":\"Hebrew\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":true},{\"code\":\"HR\",\"locale\":\"hz\",\"vernacular\":\"Otjiherero\",\"name\":\"Herero\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"HV\",\"locale\":\"hil\",\"vernacular\":\"Hiligaynon\",\"name\":\"Hiligaynon\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"HI\",\"locale\":\"hi\",\"vernacular\":\"हिंदी\",\"name\":\"Hindi\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"H\",\"locale\":\"hu\",\"vernacular\":\"magyar\",\"name\":\"Hungarian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"HDF\",\"locale\":\"hsh\",\"vernacular\":\"magyar jelnyelv\",\"name\":\"Hungarian Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"HSK\",\"locale\":\"hrx\",\"vernacular\":\"Hunsrik\",\"name\":\"Hunsrik\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"IA\",\"locale\":\"iba\",\"vernacular\":\"Iban\",\"name\":\"Iban\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"IG\",\"locale\":\"ibg\",\"vernacular\":\"Ibanag\",\"name\":\"Ibanag\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"IBI\",\"locale\":\"yom_x_ibi\",\"vernacular\":\"Ibinda\",\"name\":\"Ibinda\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"IC\",\"locale\":\"is\",\"vernacular\":\"íslenska\",\"name\":\"Icelandic\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"IB\",\"locale\":\"ig\",\"vernacular\":\"Igbo\",\"name\":\"Igbo\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"IL\",\"locale\":\"ilo\",\"vernacular\":\"Iloko\",\"name\":\"Iloko\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"INS\",\"locale\":\"ins\",\"vernacular\":\"Indian Sign Language\",\"name\":\"Indian Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"IN\",\"locale\":\"id\",\"vernacular\":\"Indonesia\",\"name\":\"Indonesian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"INI\",\"locale\":\"inl\",\"vernacular\":\"Bahasa Isyarat Indonesia\",\"name\":\"Indonesian Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"IS\",\"locale\":\"iso\",\"vernacular\":\"Isoko\",\"name\":\"Isoko\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"I\",\"locale\":\"it\",\"vernacular\":\"Italiano\",\"name\":\"Italian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"ISL\",\"locale\":\"ise\",\"vernacular\":\"Lingua dei segni italiana\",\"name\":\"Italian Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"J\",\"locale\":\"ja\",\"vernacular\":\"日本語\",\"name\":\"Japanese\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"JSL\",\"locale\":\"jsl\",\"vernacular\":\"日本手話\",\"name\":\"Japanese Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"JA\",\"locale\":\"jv\",\"vernacular\":\"Jawa\",\"name\":\"Javanese\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"KBV\",\"locale\":\"kea\",\"vernacular\":\"Kabuverdianu\",\"name\":\"Kabuverdianu\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"KBY\",\"locale\":\"kab\",\"vernacular\":\"Taqbaylit\",\"name\":\"Kabyle\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"KA\",\"locale\":\"kn\",\"vernacular\":\"Kannada (ಕನ್ನಡ)\",\"name\":\"Kannada\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"KR\",\"locale\":\"ksw\",\"vernacular\":\"ကညီ(စှီၤ)ကျိာ်\",\"name\":\"Karen (S'gaw)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"AZ\",\"locale\":\"kk\",\"vernacular\":\"қазақ\",\"name\":\"Kazakh\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"GK\",\"locale\":\"kek\",\"vernacular\":\"Q’eqchi’\",\"name\":\"Kekchi\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"KB\",\"locale\":\"kam\",\"vernacular\":\"Kikamba\",\"name\":\"Kikamba\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"KG\",\"locale\":\"kwy\",\"vernacular\":\"Kikongo\",\"name\":\"Kikongo\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"KQ\",\"locale\":\"ki\",\"vernacular\":\"Gĩkũyũ\",\"name\":\"Kikuyu\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"KU\",\"locale\":\"lu\",\"vernacular\":\"Kiluba\",\"name\":\"Kiluba\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"KIM\",\"locale\":\"kmb\",\"vernacular\":\"Kimbundu\",\"name\":\"Kimbundu\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"YW\",\"locale\":\"rw\",\"vernacular\":\"Ikinyarwanda\",\"name\":\"Kinyarwanda\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"KZ\",\"locale\":\"ky\",\"vernacular\":\"кыргыз\",\"name\":\"Kirghiz\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"GB\",\"locale\":\"gil\",\"vernacular\":\"Kiribati\",\"name\":\"Kiribati\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"RU\",\"locale\":\"run\",\"vernacular\":\"Ikirundi\",\"name\":\"Kirundi\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"KI\",\"locale\":\"kss\",\"vernacular\":\"Kisiei\",\"name\":\"Kisi\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"KSN\",\"locale\":\"sop\",\"vernacular\":\"Kisongye\",\"name\":\"Kisonge\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"MK\",\"locale\":\"kg\",\"vernacular\":\"Kikongo (Rép. dém. du congo)\",\"name\":\"Kongo\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"KO\",\"locale\":\"ko\",\"vernacular\":\"한국어\",\"name\":\"Korean\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"KSL\",\"locale\":\"kvk\",\"vernacular\":\"한국 수화\",\"name\":\"Korean Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"KRI\",\"locale\":\"kri\",\"vernacular\":\"Krio\",\"name\":\"Krio\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"RDU\",\"locale\":\"kmr_x_rdu\",\"vernacular\":\"Kurdî Kurmancî (Kavkazûs)\",\"name\":\"Kurdish Kurmanji (Caucasus)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"RDC\",\"locale\":\"kmr_cyrl\",\"vernacular\":\"К′öрди Кöрманщи (Кирили)\",\"name\":\"Kurdish Kurmanji (Cyrillic)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"WG\",\"locale\":\"kwn\",\"vernacular\":\"Rukwangali\",\"name\":\"Kwangali\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"KY\",\"locale\":\"kj\",\"vernacular\":\"Oshikwanyama\",\"name\":\"Kwanyama\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"LT\",\"locale\":\"lv\",\"vernacular\":\"Latviešu\",\"name\":\"Latvian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"LI\",\"locale\":\"ln\",\"vernacular\":\"Lingala\",\"name\":\"Lingala\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"L\",\"locale\":\"lt\",\"vernacular\":\"lietuvių\",\"name\":\"Lithuanian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"LWX\",\"locale\":\"pdt\",\"vernacular\":\"Plautdietsch\",\"name\":\"Low German\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"LU\",\"locale\":\"lg\",\"vernacular\":\"Luganda\",\"name\":\"Luganda\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"LO\",\"locale\":\"luo\",\"vernacular\":\"Dholuo\",\"name\":\"Luo\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"LV\",\"locale\":\"lue\",\"vernacular\":\"Luvale\",\"name\":\"Luvale\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"LX\",\"locale\":\"lb\",\"vernacular\":\"Lëtzebuergesch\",\"name\":\"Luxembourgish\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"MC\",\"locale\":\"mk\",\"vernacular\":\"македонски\",\"name\":\"Macedonian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"TTM\",\"locale\":\"mzc\",\"vernacular\":\"Tenin’ny Tanana Malagasy\",\"name\":\"Madagascar Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"MG\",\"locale\":\"mg\",\"vernacular\":\"Malagasy\",\"name\":\"Malagasy\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"MSL\",\"locale\":\"sgn_mw\",\"vernacular\":\"Chinenero Chamanja cha ku Malawi\",\"name\":\"Malawi Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"ML\",\"locale\":\"ms\",\"vernacular\":\"Melayu\",\"name\":\"Malay\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"MY\",\"locale\":\"ml\",\"vernacular\":\"മലയാളം\",\"name\":\"Malayalam\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"MT\",\"locale\":\"mt\",\"vernacular\":\"Malti\",\"name\":\"Maltese\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"MZ\",\"locale\":\"mam\",\"vernacular\":\"mam\",\"name\":\"Mam\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"MWL\",\"locale\":\"mgr\",\"vernacular\":\"Cimambwe-Lungu\",\"name\":\"Mambwe-Lungu\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"MPD\",\"locale\":\"arn\",\"vernacular\":\"mapudungun\",\"name\":\"Mapudungun\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"CE\",\"locale\":\"mfe\",\"vernacular\":\"Kreol Morisien\",\"name\":\"Mauritian Creole\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"MAY\",\"locale\":\"yua\",\"vernacular\":\"maaya\",\"name\":\"Maya\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"MAZ\",\"locale\":\"mau\",\"vernacular\":\"énná\",\"name\":\"Mazatec (Huautla)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"DU\",\"locale\":\"byv\",\"vernacular\":\"Bangangté\",\"name\":\"Medumba\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"LSM\",\"locale\":\"mfs\",\"vernacular\":\"lengua de señas mexicana\",\"name\":\"Mexican Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"MGL\",\"locale\":\"xmf\",\"vernacular\":\"მარგალური\",\"name\":\"Mingrelian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"MXG\",\"locale\":\"mxv\",\"vernacular\":\"tu’un sâví\",\"name\":\"Mixtec (Guerrero)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"KHA\",\"locale\":\"mn\",\"vernacular\":\"монгол\",\"name\":\"Mongolian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"BU\",\"locale\":\"my\",\"vernacular\":\"မြန်မာ\",\"name\":\"Myanmar\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"NHC\",\"locale\":\"ncx\",\"vernacular\":\"náhuatl del centro\",\"name\":\"Nahuatl (Central)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"NHG\",\"locale\":\"ngu\",\"vernacular\":\"náhuatl de guerrero\",\"name\":\"Nahuatl (Guerrero)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"NHH\",\"locale\":\"nch\",\"vernacular\":\"náhuatl de la huasteca\",\"name\":\"Nahuatl (Huasteca)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"NHT\",\"locale\":\"ncj\",\"vernacular\":\"náhuatl del norte de Puebla\",\"name\":\"Nahuatl (Northern Puebla)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"NV\",\"locale\":\"nv\",\"vernacular\":\"Diné Bizaad\",\"name\":\"Navajo\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"NBL\",\"locale\":\"nr\",\"vernacular\":\"IsiNdebele\",\"name\":\"Ndebele\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"NBZ\",\"locale\":\"nd\",\"vernacular\":\"Ndebele (Zimbabwe)\",\"name\":\"Ndebele (Zimbabwe)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"OD\",\"locale\":\"ng\",\"vernacular\":\"Oshindonga\",\"name\":\"Ndonga\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"NP\",\"locale\":\"ne\",\"vernacular\":\"नेपाली\",\"name\":\"Nepali\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"NGB\",\"locale\":\"gym\",\"vernacular\":\"ngäbere\",\"name\":\"Ngabere\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"NGL\",\"locale\":\"nba\",\"vernacular\":\"Ngangela\",\"name\":\"Ngangela\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"NMB\",\"locale\":\"nnh\",\"vernacular\":\"Mbouda\",\"name\":\"Ngiemboon\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"NI\",\"locale\":\"nia\",\"vernacular\":\"Nias\",\"name\":\"Nias\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"NGP\",\"locale\":\"pcm\",\"vernacular\":\"Nigerian Pidgin\",\"name\":\"Nigerian Pidgin\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"N\",\"locale\":\"no\",\"vernacular\":\"Norsk\",\"name\":\"Norwegian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"NK\",\"locale\":\"nyk\",\"vernacular\":\"Nyaneka\",\"name\":\"Nyaneka\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"OSS\",\"locale\":\"os\",\"vernacular\":\"ирон\",\"name\":\"Ossetian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"OT\",\"locale\":\"tll\",\"vernacular\":\"Ɔtɛtɛla\",\"name\":\"Otetela\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"OTM\",\"locale\":\"ote\",\"vernacular\":\"Ñañu\",\"name\":\"Otomi (Mezquital Valley)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"PN\",\"locale\":\"pag\",\"vernacular\":\"Pangasinan\",\"name\":\"Pangasinan\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"PAA\",\"locale\":\"pap_x_paa\",\"vernacular\":\"Papiamento (Aruba)\",\"name\":\"Papiamento (Aruba)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"PA\",\"locale\":\"pap\",\"vernacular\":\"Papiamentu (Kòrsou)\",\"name\":\"Papiamento (Curaçao)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"LSP\",\"locale\":\"pys\",\"vernacular\":\"lenguaje de señas paraguayo\",\"name\":\"Paraguayan Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"PR\",\"locale\":\"fa\",\"vernacular\":\"فارسی\",\"name\":\"Persian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":true},{\"code\":\"SPE\",\"locale\":\"prl\",\"vernacular\":\"lenguaje de señas peruano\",\"name\":\"Peruvian Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"PCM\",\"locale\":\"wes\",\"vernacular\":\"Pidgin for Cameroon\",\"name\":\"Pidgin (Cameroon)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"PGW\",\"locale\":\"wes_x_pgw\",\"vernacular\":\"Pidgin (West Africa)\",\"name\":\"Pidgin (West Africa)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"P\",\"locale\":\"pl\",\"vernacular\":\"polski\",\"name\":\"Polish\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"PDF\",\"locale\":\"pso\",\"vernacular\":\"polski język migowy\",\"name\":\"Polish Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"T\",\"locale\":\"pt\",\"vernacular\":\"Português\",\"name\":\"Portuguese\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"TPO\",\"locale\":\"jw_tpo\",\"vernacular\":\"Português (Portugal)\",\"name\":\"Portuguese (Portugal)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"LGP\",\"locale\":\"psr\",\"vernacular\":\"Língua Gestual Portuguesa\",\"name\":\"Portuguese Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"PJN\",\"locale\":\"pnb\",\"vernacular\":\"پنجابی (شاہ مُکھی)\",\"name\":\"Punjabi (Shahmukhi)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":true},{\"code\":\"QUN\",\"locale\":\"que\",\"vernacular\":\"Quechua (Ancash)\",\"name\":\"Quechua (Ancash)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"QUA\",\"locale\":\"quy\",\"vernacular\":\"Quechua (Ayacucho)\",\"name\":\"Quechua (Ayacucho)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"QUB\",\"locale\":\"qu\",\"vernacular\":\"Quechua (Bolivia)\",\"name\":\"Quechua (Bolivia)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"QU\",\"locale\":\"quz\",\"vernacular\":\"quechua (Cusco)\",\"name\":\"Quechua (Cuzco)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"QIC\",\"locale\":\"qug\",\"vernacular\":\"quichua (chimborazo)\",\"name\":\"Quichua (Chimborazo)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"QII\",\"locale\":\"qvi\",\"vernacular\":\"quichua (imbabura)\",\"name\":\"Quichua (Imbabura)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"M\",\"locale\":\"ro\",\"vernacular\":\"Română\",\"name\":\"Romanian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"LMG\",\"locale\":\"rms\",\"vernacular\":\"Limbajul semnelor românesc\",\"name\":\"Romanian Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"RM\",\"locale\":\"rmn_x_rm\",\"vernacular\":\"romane (Makedonija)\",\"name\":\"Romany (Macedonia)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"RMC\",\"locale\":\"rmn_cyrl\",\"vernacular\":\"романе (Македонија) кирилица\",\"name\":\"Romany (Macedonia) Cyrillic\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"RMG\",\"locale\":\"rmn_x_rmg\",\"vernacular\":\"Ρομανί (Νότια Ελλάδα)\",\"name\":\"Romany (Southern Greece)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"RMV\",\"locale\":\"rmy_x_rmv\",\"vernacular\":\"романи (влахитско, Россия)\",\"name\":\"Romany (Vlax, Russia)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"U\",\"locale\":\"ru\",\"vernacular\":\"русский\",\"name\":\"Russian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"RSL\",\"locale\":\"rsl\",\"vernacular\":\"русский жестовый\",\"name\":\"Russian Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"SM\",\"locale\":\"sm\",\"vernacular\":\"Faa-Samoa\",\"name\":\"Samoan\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"SRM\",\"locale\":\"srm\",\"vernacular\":\"Saamakatöngö\",\"name\":\"Saramaccan\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"SE\",\"locale\":\"nso\",\"vernacular\":\"Sepedi\",\"name\":\"Sepedi\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"SPL\",\"locale\":\"nso_x_spl\",\"vernacular\":\"Sepulana\",\"name\":\"Sepulana\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"SB\",\"locale\":\"sr_cyrl\",\"vernacular\":\"српски (ћирилица)\",\"name\":\"Serbian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"SBO\",\"locale\":\"sr_latn\",\"vernacular\":\"srpski (latinica)\",\"name\":\"Serbian (Roman)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"SU\",\"locale\":\"st\",\"vernacular\":\"Sesotho (Lesotho)\",\"name\":\"Sesotho (Lesotho)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"SSA\",\"locale\":\"st_za\",\"vernacular\":\"Sesotho (South Africa)\",\"name\":\"Sesotho (South Africa)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"TN\",\"locale\":\"tn\",\"vernacular\":\"Setswana\",\"name\":\"Setswana\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"SC\",\"locale\":\"crs\",\"vernacular\":\"Kreol Seselwa\",\"name\":\"Seychelles Creole\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"CA\",\"locale\":\"sn\",\"vernacular\":\"Shona\",\"name\":\"Shona\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"SK\",\"locale\":\"loz\",\"vernacular\":\"Silozi\",\"name\":\"Silozi\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"SN\",\"locale\":\"si\",\"vernacular\":\"සිංහල\",\"name\":\"Sinhala\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"V\",\"locale\":\"sk\",\"vernacular\":\"slovenčina\",\"name\":\"Slovak\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"VSL\",\"locale\":\"svk\",\"vernacular\":\"slovenský posunkový jazyk\",\"name\":\"Slovak Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"SV\",\"locale\":\"sl\",\"vernacular\":\"slovenščina\",\"name\":\"Slovenian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"SP\",\"locale\":\"pis\",\"vernacular\":\"Solomon Islands Pidgin\",\"name\":\"Solomon Islands Pidgin\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"SAS\",\"locale\":\"sfs\",\"vernacular\":\"South African Sign Language\",\"name\":\"South African Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"S\",\"locale\":\"es\",\"vernacular\":\"español\",\"name\":\"Spanish\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"LSE\",\"locale\":\"ssp\",\"vernacular\":\"lengua de signos española\",\"name\":\"Spanish Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"SR\",\"locale\":\"srn\",\"vernacular\":\"Sranantongo\",\"name\":\"Sranantongo\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"SD\",\"locale\":\"su\",\"vernacular\":\"Sunda\",\"name\":\"Sunda\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"SW\",\"locale\":\"sw\",\"vernacular\":\"Kiswahili\",\"name\":\"Swahili\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"ZS\",\"locale\":\"swc\",\"vernacular\":\"Kiswahili (Congo)\",\"name\":\"Swahili (Congo)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"SWI\",\"locale\":\"ss\",\"vernacular\":\"SiSwati\",\"name\":\"Swati\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"Z\",\"locale\":\"sv\",\"vernacular\":\"Svenska\",\"name\":\"Swedish\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"SSL\",\"locale\":\"swl\",\"vernacular\":\"Svenskt teckenspråk\",\"name\":\"Swedish Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"XSW\",\"locale\":\"gsw\",\"vernacular\":\"Schweizerdeutsch\",\"name\":\"Swiss German\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"TG\",\"locale\":\"tl\",\"vernacular\":\"Tagalog\",\"name\":\"Tagalog\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"TH\",\"locale\":\"ty\",\"vernacular\":\"Tahiti\",\"name\":\"Tahitian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"TJ\",\"locale\":\"tg\",\"vernacular\":\"тоҷикӣ\",\"name\":\"Tajiki\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"TAL\",\"locale\":\"vec\",\"vernacular\":\"Talian\",\"name\":\"Talian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"TL\",\"locale\":\"ta\",\"vernacular\":\"தமிழ்\",\"name\":\"Tamil\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"TND\",\"locale\":\"tdx\",\"vernacular\":\"Tandroy\",\"name\":\"Tandroy\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"TNK\",\"locale\":\"xmv\",\"vernacular\":\"Tankarana\",\"name\":\"Tankarana\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"TRS\",\"locale\":\"tsz\",\"vernacular\":\"Purépecha\",\"name\":\"Tarascan\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"TAT\",\"locale\":\"tt\",\"vernacular\":\"татар\",\"name\":\"Tatar\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"TU\",\"locale\":\"te\",\"vernacular\":\"తెలుగు\",\"name\":\"Telugu\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"TTP\",\"locale\":\"tdt\",\"vernacular\":\"Tetun Dili\",\"name\":\"Tetun Dili\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"SI\",\"locale\":\"th\",\"vernacular\":\"ไทย\",\"name\":\"Thai\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"SIL\",\"locale\":\"tsq\",\"vernacular\":\"ภาษามือไทย\",\"name\":\"Thai Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"TV\",\"locale\":\"tiv\",\"vernacular\":\"Tiv\",\"name\":\"Tiv\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"TLN\",\"locale\":\"tcf\",\"vernacular\":\"me̱ʼpha̱a̱\",\"name\":\"Tlapanec\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"TJO\",\"locale\":\"toj\",\"vernacular\":\"tojol-abʼal\",\"name\":\"Tojolabal\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"TO\",\"locale\":\"to\",\"vernacular\":\"Faka-Tonga\",\"name\":\"Tongan\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"TOT\",\"locale\":\"top\",\"vernacular\":\"totonaco\",\"name\":\"Totonac\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"SH\",\"locale\":\"lua\",\"vernacular\":\"Tshiluba\",\"name\":\"Tshiluba\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"TS\",\"locale\":\"ts\",\"vernacular\":\"Xitsonga\",\"name\":\"Tsonga\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"TK\",\"locale\":\"tr\",\"vernacular\":\"Türkçe\",\"name\":\"Turkish\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"TMR\",\"locale\":\"tk\",\"vernacular\":\"türkmen\",\"name\":\"Turkmen\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"VL\",\"locale\":\"tvl\",\"vernacular\":\"Tuvalu\",\"name\":\"Tuvaluan\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"TW\",\"locale\":\"tw\",\"vernacular\":\"Twi\",\"name\":\"Twi\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"TZE\",\"locale\":\"tzh\",\"vernacular\":\"tseltal\",\"name\":\"Tzeltal\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"TZO\",\"locale\":\"tzo\",\"vernacular\":\"tsotsil\",\"name\":\"Tzotzil\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"UG\",\"locale\":\"ug_cyrl\",\"vernacular\":\"Уйғур (кирилл йезиғи)\",\"name\":\"Uighur (Cyrillic)\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"K\",\"locale\":\"uk\",\"vernacular\":\"українська\",\"name\":\"Ukrainian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"UB\",\"locale\":\"umb\",\"vernacular\":\"Umbundu\",\"name\":\"Umbundu\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"UD\",\"locale\":\"ur\",\"vernacular\":\"اُردو\",\"name\":\"Urdu\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":true},{\"code\":\"UR\",\"locale\":\"urh\",\"vernacular\":\"Urhobo\",\"name\":\"Urhobo\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"DR\",\"locale\":\"rnd\",\"vernacular\":\"Uruund\",\"name\":\"Uruund\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"UZ\",\"locale\":\"uz_cyrl\",\"vernacular\":\"ўзбекча\",\"name\":\"Uzbek\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"VLC\",\"locale\":\"ca_x_vlc\",\"vernacular\":\"valencià\",\"name\":\"Valencian\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"VE\",\"locale\":\"ve\",\"vernacular\":\"Luvenda\",\"name\":\"Venda\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"LSV\",\"locale\":\"vsl\",\"vernacular\":\"lengua de señas venezolana\",\"name\":\"Venezuelan Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"VZ\",\"locale\":\"skg_x_vz\",\"vernacular\":\"Vezo\",\"name\":\"Vezo\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"VT\",\"locale\":\"vi\",\"vernacular\":\"Việt\",\"name\":\"Vietnamese\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"SA\",\"locale\":\"war\",\"vernacular\":\"Waray-Waray\",\"name\":\"Waray-Waray\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"W\",\"locale\":\"cy\",\"vernacular\":\"Cymraeg\",\"name\":\"Welsh\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"XO\",\"locale\":\"xh\",\"vernacular\":\"IsiXhosa\",\"name\":\"Xhosa\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"BM\",\"locale\":\"ybb\",\"vernacular\":\"Dschang\",\"name\":\"Yemba\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"YR\",\"locale\":\"yo\",\"vernacular\":\"Yorùbá\",\"name\":\"Yoruba\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false},{\"code\":\"ZSL\",\"locale\":\"zib\",\"vernacular\":\"Zimbabwe Sign Language\",\"name\":\"Zimbabwe Sign Language\",\"isLangPair\":false,\"isSignLanguage\":true,\"isRTL\":false},{\"code\":\"ZU\",\"locale\":\"zu\",\"vernacular\":\"IsiZulu\",\"name\":\"Zulu\",\"isLangPair\":false,\"isSignLanguage\":false,\"isRTL\":false}]}";

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    MenuItem bericht;
    public static boolean wannachange = false;

    private ActionBarDrawerToggle actionbartoggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(getResources().getString(R.string.title_section5));
   /*     if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/

        sp = getSharedPreferences("MainActivity", MODE_PRIVATE);
        editor = sp.edit();

        //builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder = new AlertDialog.Builder(Settings.this);

        SettingsStorageModule ssm = new SettingsStorageModule();
        SettingsInputModule sim = new SettingsInputModule();


        /////////////////DRAWER/////////////////////////////////////////
        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_5);
        setSupportActionBar(toolbar);
        toolbar.bringToFront();

        Drawer.addDrawer(this, toolbar, 8);

        ////////////////////DRAWER/////////////////////////////////////////////////////////////////////////////

        MaterialEditTextPreference berichtLayoutEdit = findViewById(R.id.report_layout_settings);
        berichtLayoutEdit.setUserInputModule(sim);
        berichtLayoutEdit.setStorageModule(ssm);
        findViewById(R.id.report_layout_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
            findViewById(R.id.shortcut_entry).setVisibility(View.GONE);
        }

        /*findViewById(R.id.shortcut_entry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {getString(R.string.title_section1),getString(R.string.title_section3),getString(R.string.title_section4),getString(R.string.title_section7),getString(R.string.title_tt), getString(R.string.title_section9)};
// arraylist to keep the selected items
                final ArrayList<Integer> seletedItems=new ArrayList<>();

                AlertDialog dialog = new AlertDialog.Builder(Settings.this)
                        .setTitle(getString(R.string.shortcuts_dialog_title))
                        .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                                if(seletedItems.size() +1 == 6){
                                    Toast.makeText(Settings.this, getString(R.string.shortcuts_max_reached), Toast.LENGTH_SHORT).show();
                                    ((AlertDialog) dialog).getListView().setItemChecked(indexSelected, false);
                                    return;
                                }
                                if (isChecked) {
                                    seletedItems.add(indexSelected);
                                } else if (seletedItems.contains(indexSelected)) {
                                    seletedItems.remove(Integer.valueOf(indexSelected));
                                }
                            }
                        }).setPositiveButton(getString(R.string.action_save), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                String s = "";
                                for(int i : seletedItems){
                                    s = s+i;
                                }
                                editor.putString("SHORTCUTS", s);
                                editor.commit();
                                Shortcuts.updateShortcuts(s, Settings.this, false);
                            }
                        }).setNegativeButton(getString(R.string.delete_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {}
                        }).create();
                dialog.show();
            }
        });*/

        MaterialSwitchPreference privateModeSwitch = findViewById(R.id.report_private_mode);
        privateModeSwitch.setStorageModule(ssm);

/*
        findViewById(R.id.report_private_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this)
                        .setTitle(getString(R.string.report_private_mode))
                        .setMessage(getString(R.string.report_private_onoff))
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.report_private_on), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                editor.putBoolean("private_mode", true);
                                editor.commit();
                            }
                        }).setNegativeButton(getString(R.string.report_private_off), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                editor.putBoolean("private_mode", false);
                                editor.commit();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });*/

        findViewById(R.id.language_empf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {getString(R.string.language_default), "English", "German", "Italian", "French", "Polish", "Turkish", "Thai", "Greek"};
                final String[] langcodes = {"0", "en", "de", "it", "fr", "pl", "tr", "th", "el"};
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                builder.setTitle(R.string.language);

                int checkedItem = 0;
                if(sp.contains("sample_presentations_locale")){
                    checkedItem = Arrays.asList(langcodes).indexOf(sp.getString("sample_presentations_locale", "0"));
                }


                builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if(items[item].toString().equalsIgnoreCase(getString(R.string.language_default))){
                            editor.remove("sample_presentations_locale");
                        }else{
                            editor.putString("sample_presentations_locale", langcodes[item]);
                        }
                        editor.commit();
                        dialog.dismiss();
                    }
                });



                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        findViewById(R.id.language_tt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final HashMap<String, String> langCode = new HashMap<String, String>();
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                builder.setTitle(R.string.language);

                langCode.put(getString(R.string.language_default), "0");
                try {
                    JSONObject o = new JSONObject(jwlangs);
                    JSONArray arr = o.getJSONArray("languages");

                    for (int in = 0 ; in < arr.length(); in++) {
                        JSONObject langObj = arr.getJSONObject(in);
                        String name = langObj.getString("name");
                        String loc = langObj.getString("locale");
                        if(!langObj.getBoolean("isSignLanguage"))
                            langCode.put(name, loc);
                    }

                    final SortedSet<String> keysSort = new TreeSet<>(langCode.keySet());

                    int checkedItem = 0;
                    if(sp.contains("tt_locale")){
                        checkedItem = Arrays.asList(keysSort.toArray(new String[]{})).indexOf(sp.getString("tt_locale", "0"));
                    }else{
                        checkedItem = Arrays.asList(keysSort.toArray(new String[]{})).indexOf(getString(R.string.language_default));
                    }




                    builder.setSingleChoiceItems(keysSort.toArray(new String[]{}), checkedItem, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            if(keysSort.toArray(new String[]{})[item].toString().equalsIgnoreCase(getString(R.string.language_default))){
                                editor.remove("tt_locale");
                            }else{
                                editor.putString("tt_locale", langCode.get(keysSort.toArray(new String[]{})[item]));
                            }
                            editor.commit();



                            Intent intent = new Intent(Settings.this, TagestextWidget.class);
                            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
// Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
// since it seems the onUpdate() is only fired on that:
                            int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), TagestextWidget.class));
                            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                            sendBroadcast(intent);

                            dialog.dismiss();
                        }
                    });



                    AlertDialog alert = builder.create();
                    alert.show();



                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder
                        .setTitle(getResources().getString(R.string.resetdialog_title))
                        .setMessage(getResources().getString(R.string.resetdialog_message))
                        .setInverseBackgroundForced(true)
                        .setIcon(null)
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                editor.clear();
                                editor.commit();

                                SharedPreferences sp1 = getSharedPreferences("Splash", MODE_PRIVATE);
                                SharedPreferences.Editor editor1 = sp1.edit();
                                editor1.clear();
                                editor1.commit();

                                SharedPreferences sp2 = getSharedPreferences("MainActivity3", MODE_PRIVATE);
                                SharedPreferences.Editor editor2 = sp2.edit();
                                editor2.clear();
                                editor2.commit();



                                final Snackbar snackbar = Snackbar
                                        .make(findViewById(R.id.drawer_10), getString(R.string.data_cleared), Snackbar.LENGTH_SHORT);
                                snackbar.setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        snackbar.dismiss();
                                    }
                                });

                                snackbar.show();
                            }
                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .show();
            }
        });

        findViewById(R.id.impressum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://dienstapp.raffaelhahn.de/impressum.html#impr";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        findViewById(R.id.licenses).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LicenserDialog(Settings.this, R.style.MaterialBaseTheme_Dialog)
                        .setTitle(getString(R.string.licenses))
                        .setLibrary(new Library("AndroidX Support Libraries",
                                "https://developer.android.com/jetpack/androidx",
                                License.Companion.getAPACHE2()))
                        .setLibrary(new Library("CompactCalendarView",
                                "https://github.com/SundeepK/CompactCalendarView",
                                License.Companion.getMIT()))
                        .setLibrary(new Library("Licenser",
                                "https://github.com/marcoscgdev/Licenser",
                                License.Companion.getMIT()))
                        .setLibrary(new Library("Material Components",
                                "https://github.com/material-components/material-components-android",
                                License.Companion.getAPACHE2()))
                        .setLibrary(new Library("CircleImageView",
                                "https://github.com/hdodenhof/CircleImageView",
                                License.Companion.getAPACHE2()))
                        .setLibrary(new Library("AutoFitTextView",
                                "https://github.com/grantland/android-autofittextview",
                                License.Companion.getAPACHE2()))
                        .setLibrary(new Library("AutoFitTextView",
                                "https://github.com/AndroidDeveloperLB/AutoFitTextView",
                                License.Companion.getAPACHE2()))
                        .setLibrary(new Library("Android Sliding Up Panel",
                                "https://github.com/umano/AndroidSlidingUpPanel",
                                License.Companion.getAPACHE2()))
                        .setLibrary(new Library("Picasso",
                                "https://github.com/square/picasso",
                                License.Companion.getAPACHE2()))
                        .setLibrary(new Library("CircleView",
                                "https://github.com/jakob-grabner/Circle-Progress-View",
                                License.Companion.getMIT()))
                        .setLibrary(new Library("MaterialDrawer",
                                "https://github.com/mikepenz/MaterialDrawer",
                                License.Companion.getAPACHE2()))
                        .setLibrary(new Library("CircularReveal",
                                "https://github.com/ozodrukh/CircularReveal",
                                License.Companion.getMIT()))
                        .setLibrary(new Library("RSS Parser",
                                "https://github.com/prof18/RSS-Parser",
                                License.Companion.getAPACHE2()))
                        .setLibrary(new Library("Jackson Core",
                                "https://github.com/FasterXML/jackson-core",
                                License.Companion.getAPACHE2()))
                        .setLibrary(new Library("MaterialPreferences",
                                "https://github.com/yarolegovich/MaterialPreferences",
                                License.Companion.getAPACHE2()))
                        .setLibrary(new Library("Month and Year Picker",
                                "https://github.com/dewinjm/monthyear-picker",
                                License.Companion.getAPACHE2()))
                        .setLibrary(new Library("Kotlin",
                                "https://github.com/JetBrains/kotlin",
                                License.Companion.getAPACHE2()))

                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
                //startActivity(new Intent(getApplicationContext(), Licenses.class));
            }
        });

        findViewById(R.id.dsgvo_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), DSGVOInfo.class);
                i.putExtra("hastoaccept", false);
                startActivity(i);
            }
        });

        findViewById(R.id.export).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Export.exportGlobal(Settings.this, Export.Format.DIENSTAPP_GLOBAL);
            }
        });

        if(!sp.contains("CalendarSyncActive")) {
            findViewById(R.id.calendar_gcal_reset).setVisibility(View.GONE);
        }

        findViewById(R.id.calendar_gcal_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sp.contains("CalendarSyncActive")) {

                    if(sp.getBoolean("CalendarSyncActive", false)) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:

                                        editor.remove("CalendarSyncActive");
                                        editor.remove("CalendarSyncGacc");
                                        editor.remove("CalendarSyncTCID");
                                        editor.remove("CalendarSync");
                                        editor.commit();
                                        Toast.makeText(Settings.this, R.string.calendar_gcal_reset_to_default, Toast.LENGTH_LONG).show();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                findViewById(R.id.calendar_gcal_reset).setVisibility(View.GONE);
                                            }
                                        });
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                        builder.setTitle(R.string.calendar_gcal_remove_title).setMessage(R.string.calendar_gcal_remove_msg).setPositiveButton(R.string.yes, dialogClickListener)
                                .setNegativeButton(R.string.no, dialogClickListener).show();
                    }else{
                        Toast.makeText(Settings.this, R.string.calendar_gcal_reset_to_default, Toast.LENGTH_LONG).show();
                        editor.remove("CalendarSyncActive");
                        editor.remove("CalendarSyncGacc");
                        editor.remove("CalendarSyncTCID");
                        editor.remove("CalendarSync");
                        editor.commit();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.calendar_gcal_reset).setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }
        });

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            findViewById(R.id.calendar_notification).setVisibility(View.GONE);
        }

        findViewById(R.id.calendar_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent = new Intent(android.provider.Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                            .putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, Settings.this.getPackageName())
                            .putExtra(android.provider.Settings.EXTRA_CHANNEL_ID, "calendar");
                    startActivity(intent);
                }else {
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");

                    intent.putExtra("app_package", getPackageName());
                    intent.putExtra("app_uid", getApplicationInfo().uid);

                    intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());

                    startActivity(intent);
                }
            }
        });

        findViewById(R.id.calendar_time_of_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Settings.this);
                LayoutInflater inflater = Settings.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.notification_time_popup, null);
                dialogBuilder.setView(dialogView);

                final EditText edt = (EditText) dialogView.findViewById(R.id.time);
                final Spinner spin = (Spinner) dialogView.findViewById(R.id.unit);

                String[] units = {getResources().getString(R.string.calendar_notification_hour), getResources().getString(R.string.calendar_notification_day)};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Settings.this, android.R.layout.simple_list_item_1, units);
                spin.setAdapter(adapter);

                edt.setText(sp.getInt("Calendar_Time", 1)+"");
                int i = sp.getInt("Calendar_Unit", 0);//0 hour; 1 day;
                spin.setSelection(i);

                dialogBuilder.setTitle(getString(R.string.calendar_time_of_notification));
                dialogBuilder.setPositiveButton(getString(R.string.action_save), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        editor.putInt("Calendar_Time", Integer.parseInt(edt.getText().toString()));
                        editor.putInt("Calendar_Unit", spin.getSelectedItemPosition());
                        editor.remove("Calendar_Shown");
                        editor.commit();
                    }
                });
                AlertDialog b = dialogBuilder.create();
                b.show();


            }
        });

    }


    public class SettingsInputModule implements UserInputModule {

        @Override
        public void showEditTextInput(String key, CharSequence title, CharSequence defaultValue, final Listener<String> listener) {
            if(key.equalsIgnoreCase("report_layout")){

                AlertDialog.Builder alert = new AlertDialog.Builder(Settings.this);

                alert.setTitle(getString(R.string.report_layout_settings));
                alert.setMessage(getString(R.string.report_layout_msg));

                final LinearLayout layout = new LinearLayout(Settings.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                View child = getLayoutInflater().inflate(R.layout.list_bericht, null);
                layout.addView(child); // Notice this is an add method

                final Spinner dropdown = new Spinner(Settings.this);
                String[] items = new String[]{getString(R.string.report_layout_1), getString(R.string.report_layout_2)};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(Settings.this, android.R.layout.simple_spinner_dropdown_item, items);
                dropdown.setAdapter(adapter);
                layout.addView(dropdown); // Another add method
                alert.setView(layout);
                dropdown.setSelection(sp.getInt("report_layout", 0));

                dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if(i == 0){
                            layout.removeViewAt(0);
                            View child = getLayoutInflater().inflate(R.layout.list_bericht, null);
                            layout.addView(child, 0);
                            child.findViewById(R.id.list_bericht_background).setBackgroundColor(Color.WHITE);

                        }else if(i == 1){
                            layout.removeViewAt(0);
                            View child = getLayoutInflater().inflate(R.layout.list_bericht_tiny, null);
                            layout.addView(child, 0);
                            child.findViewById(R.id.list_bericht_background).setBackgroundColor(Color.WHITE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                alert.setPositiveButton(getString(R.string.action_save), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int i = dropdown.getSelectedItemPosition();
                        editor.putInt("report_layout", i);
                        editor.commit();
                        if(i == 0) {
                            listener.onInput(Settings.this.getString(R.string.report_layout_1));
                        }else{
                            listener.onInput(Settings.this.getString(R.string.report_layout_2));
                        }
                    }
                });

                alert.show();

            }
        }

        @Override
        public void showSingleChoiceInput(String key, CharSequence title, CharSequence[] displayItems, CharSequence[] values, int selected, Listener<String> listener) {

        }

        @Override
        public void showMultiChoiceInput(String key, CharSequence title, CharSequence[] displayItems, CharSequence[] values, boolean[] defaultSelection, Listener<Set<String>> listener) {

        }

        @Override
        public void showColorSelectionInput(String key, CharSequence title, int defaultColor, Listener<Integer> color) {

        }
    }


    public class SettingsStorageModule implements StorageModule {

        @Override
        public void saveBoolean(String key, boolean value) {
            if(key.equalsIgnoreCase("private_mode")) {
                editor.putBoolean("private_mode", value);
            }
            editor.commit();
        }

        @Override
        public void saveString(String key, String value) {

        }

        @Override
        public void saveInt(String key, int value) {

        }

        @Override
        public void saveStringSet(String key, Set<String> value) {

        }

        @Override
        public boolean getBoolean(String key, boolean defaultVal) {
            if(key.equalsIgnoreCase("private_mode")) {
                return sp.getBoolean("private_mode", defaultVal);
            }
            return false;
        }

        @Override
        public String getString(String key, String defaultVal) {
            if(key.equalsIgnoreCase("report_layout")){
                if(sp.getInt("report_layout", 0) == 0) {
                    return Settings.this.getString(R.string.report_layout_1);
                }else{
                    return Settings.this.getString(R.string.report_layout_2);
                }
            }
            return null;
        }

        @Override
        public int getInt(String key, int defaultVal) {
            return 0;
        }

        @Override
        public Set<String> getStringSet(String key, Set<String> defaultVal) {
            return null;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {

        }

        @Override
        public void onRestoreInstanceState(Bundle savedState) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

}