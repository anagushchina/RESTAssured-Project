public class Urls {

    public static final String URL = "https://api.apilayer.com/currency_data";
    public static final String LIVE_ENDPOINT = "/live";
    public static final String HISTORICAL_ENDPOINT = "/historical";
    public static final String API_KEY = "apikey=LRjp0wYtp3UpeZv80wwSK1HddgCfuYiL";
    public static final String SOURCE_TEMPLATE = "&source=SOURCE_CODE";
    public static final String CURRENCY_TEMPLATE = "&currencies=CURRENCY_CODE";
    public static final String DATE_TEMPLATE = "?date=";

    public static String liveEndpointAuthorized(){
        return URL+LIVE_ENDPOINT+"?"+API_KEY;
    }

    public static String liveWithSourceCode(String sourceCode){
        String source = SOURCE_TEMPLATE.replace("SOURCE_CODE", sourceCode);
        return liveEndpointAuthorized()+source;
    }

    public static String liveWithSourceAndCurrency(String sourceCode, String currencyCode){
        String source = SOURCE_TEMPLATE.replace("SOURCE_CODE", sourceCode);
        String currency = CURRENCY_TEMPLATE.replace("CURRENCY_CODE", currencyCode);
        return liveEndpointAuthorized()+source+currency;
    }

    public static String liveWithSourceAndCurrencies(String sourceCode, String currencyCode1, String currencyCode2){
        String source = SOURCE_TEMPLATE.replace("SOURCE_CODE", sourceCode);
        String currency1 = CURRENCY_TEMPLATE.replace("CURRENCY_CODE", currencyCode1);
        String currency2 = "%2C"+currencyCode2;
        return liveEndpointAuthorized()+source+currency1+currency2;
    }

    public static String liveWithSourceAndCurrencies(String sourceCode, String currencyCode1, String currencyCode2, String currencyCode3){
        String source = SOURCE_TEMPLATE.replace("SOURCE_CODE", sourceCode);
        String currency1 = CURRENCY_TEMPLATE.replace("CURRENCY_CODE", currencyCode1);
        String currency2 = "%2C"+currencyCode2;
        String currency3 = "%2C"+currencyCode3;
        return liveEndpointAuthorized()+source+currency1+currency2+currency3;
    }


    public static String historicalEndpointAuthorized(String date){
        return URL+HISTORICAL_ENDPOINT+DATE_TEMPLATE+date+"&"+API_KEY;
    }

    public static String historicalWithSourceCode(String date, String sourceCode){
        String source = SOURCE_TEMPLATE.replace("SOURCE_CODE", sourceCode);
        return historicalEndpointAuthorized(date)+source;
    }

    public static String historicalWithSourceAndCurrency(String date, String sourceCode, String currencyCode){
        String source = SOURCE_TEMPLATE.replace("SOURCE_CODE", sourceCode);
        String currency = CURRENCY_TEMPLATE.replace("CURRENCY_CODE", currencyCode);
        return historicalEndpointAuthorized(date)+source+currency;
    }

    public static String historicalWithSourceAndCurrencies(String date, String sourceCode, String currencyCode1, String currencyCode2){
        String source = SOURCE_TEMPLATE.replace("SOURCE_CODE", sourceCode);
        String currency1 = CURRENCY_TEMPLATE.replace("CURRENCY_CODE", currencyCode1);
        String currency2 = "%2C"+currencyCode2;
        return historicalEndpointAuthorized(date)+source+currency1+currency2;
    }

    public static String historicalWithSourceAndCurrencies(String date, String sourceCode, String currencyCode1, String currencyCode2, String currencyCode3){
        String source = SOURCE_TEMPLATE.replace("SOURCE_CODE", sourceCode);
        String currency1 = CURRENCY_TEMPLATE.replace("CURRENCY_CODE", currencyCode1);
        String currency2 = "%2C"+currencyCode2;
        String currency3 = "%2C"+currencyCode3;
        return historicalEndpointAuthorized(date)+source+currency1+currency2+currency3;
    }
}
