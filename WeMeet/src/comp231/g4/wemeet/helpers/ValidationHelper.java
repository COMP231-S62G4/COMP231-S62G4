package comp231.g4.wemeet.helpers;

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
}
