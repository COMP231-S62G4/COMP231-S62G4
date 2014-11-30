package comp231.g4.wemeet.helpers;

import java.text.DecimalFormat;

import android.util.Base64;

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

	public static String EncodeString(String password) {
		//encoding string using Base64 encoder
		byte[] encodedBytes = Base64.encode(password.getBytes(), Base64.DEFAULT);
		return new String(encodedBytes);
	}
}
