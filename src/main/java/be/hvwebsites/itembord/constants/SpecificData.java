package be.hvwebsites.itembord.constants;

public class SpecificData {

    public static final String NO_STATUSBORDITEMS_YET = "Er zijn nog geen items voor het status bord !";

    public static final String NO_RUBRIEKEN_YET = "Er zijn nog geen rubrieken !";
    public static final String NO_OPVOLGINGSITEMS_YET = "Er zijn nog geen opvolgingsitems !";
    public static final String NO_LOGS_YET = "Er zijn nog geen logs !";

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

    public static String setFirstLetterCapital(String name){
        String nameCapitalized = name.toUpperCase();

        return nameCapitalized.substring(0,1).concat(name.substring(1));
    }

}
