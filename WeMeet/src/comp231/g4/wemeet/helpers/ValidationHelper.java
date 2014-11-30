package comp231.g4.wemeet.helpers;

import java.text.DecimalFormat;

public class ValidationHelper {
	public static boolean IsValidPhoneNumber(String phoneNumber) {
		// validating for length
		if (phoneNumber.length() != 10)
			return false;

		// validating for numeric
		try {
			Long.parseLong(phoneNumber);
		} catch (Exception e) {
			return false;
		}

		return true;
	}
	
	public static String SanitizePhoneNumber(String phoneNumber) {
		phoneNumber = phoneNumber.replace(" ", "");
		phoneNumber = phoneNumber.replace("-", "");
		phoneNumber = phoneNumber.replace("+", "");
		
		return phoneNumber;
	}

	public static double RoundTwoDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));
	}
}
