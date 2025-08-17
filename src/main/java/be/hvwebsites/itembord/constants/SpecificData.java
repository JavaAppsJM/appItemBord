package be.hvwebsites.itembord.constants;

public class SpecificData {

    // Toast messages
    public static final String NO_STATUSBORDITEMS_YET = "Er zijn nog geen items voor het status bord !";
    public static final String NO_LOGBOEKITEMS_YET = "Er zijn nog geen logs voor het logboek !";
    public static final String NO_RUBRIEKEN_YET = "Er zijn nog geen rubrieken !";
    public static final String NO_OPVOLGINGSITEMS_YET = "Er zijn nog geen opvolgingsitems !";
    public static final String NO_LOGS_YET = "Er zijn nog geen logs !";

    public static final String NO_PARENT_RUBRIEK = "Geen hoofdrubriek";
    public static final String NO_RUBRIEK_FILTER = "Geen rubriek filter";
    public static final String NO_OPVOLGINGSITEM_FILTER = "Geen oitem filter";
    public static final String RUBRIEK_UNKNOWN = "Rubriek onbekend !";

    public static final String TITLE_NEW_RUBRIEK = "Rubriek Toevoegen";
    public static final String TITLE_UPDATE_RUBRIEK = "Rubriek Aanpassen";
    public static final String TITLE_NEW_OPVOLGINGSITEM = "Opvolgingsitem Toevoegen";
    public static final String TITLE_UPDATE_OPVOLGINGSITEM = "Opvolgingsitem Aanpassen";
    public static final String TITLE_NEW_LOG = "Log Toevoegen";
    public static final String TITLE_UPDATE_LOG = "Log Aanpassen";

    public static final String ENTITY_TYPE_RUBRIEK = "rubriek";
    public static final String ENTITY_TYPE_OPVOLGINGSITEM = "item";
    public static final String ENTITY_TYPE_LOG = "log";

    public static final String ID_RUBRIEK = "rubriek ID";
    public static final String ID_OPVOLGINGSITEM = "opvolgingsitem ID";
    public static final String ID_LOG = "log ID";

    // Context menu items text constants Priority List
    public static final String CONTEXTMENU_EDIT = "Item Aanpassen";
    public static final String CONTEXTMENU_ROLLON = "Item Afvinken";
    public static final String CONTEXTMENU_DELAY = "Item Uitstellen";

    // Cookie Key Values
    public static final String COOKIE_RETURN_ENTITY_TYPE = "returnentitytype";
    public static final String COOKIE_RETURN_ACTION = "returnaction";
    public static final String COOKIE_RETURN_UPDATE_ID = "return Id";
    public static final String COOKIE_RUBRIEK_FILTER = "rubriekfilterId";
    public static final String COOKIE_TAB_SELECTION = "tabselection";
    public static final String COOKIE_TAB_OITEM = "tabopvolgingsitem";
    public static final String COOKIE_TAB_SUBRUB = "tabsubrubriek";
    public static final String COOKIE_TAB_LOG = "tablogboek";

    // Calling Activities
    public static final String CALLING_ACTIVITY = "callingactivity";
    public static final String ACTIVITY_MAIN = "MainActivity";
    public static final String ACTIVITY_LOGBOEK = "Logboek";
    public static final String ACTIVITY_MANAGE_RUBRIEK = "Manage Rubriek";
    public static final String ACTIVITY_EDIT_RUBRIEK = "Edit Rubriek";
    public static final String ACTIVITY_EDIT_OPVITEM = "Edit Opvolgingsitem";
    public static final String ACTIVITY_EDIT_LOG = "Edit Log";
    public static final String ACTIVITY_STATUSBORD = "Statusbord";

    // Styles
    public static final String DISPLAY_SMALL = "display small";
    public static final String DISPLAY_LARGE = "display large";
    public static final String DISPLAY_OVERDUE = "display overdue";
    public static final String DISPLAY_DUE = "display due";
    public static final String DISPLAY_FUTURE = "display future";

    public static final String STYLE_BOLD = "style bold";
    public static final String STYLE_RED = "style red";
    public static final String STYLE_ORANGE = "style orange";
    public static final String STYLE_GREEN = "style green";
    public static final String STYLE_GREY = "style grey";
    public static final String STYLE_NORMAL = "style normal";
    public static final String STYLE_STATUSBORD = "style status";

    // Common Method
    public static String setFirstLetterCapital(String name){
        if (!name.equals("")){
            String nameCapitalized = name.toUpperCase();
            return nameCapitalized.substring(0,1).concat(name.substring(1));
        }else {
            return "";
        }
    }

}
