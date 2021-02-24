package beans;

public class GeocoderAddressBean {
	
	private String address,city ,state ,country ,postalCode ,knownName ;

	public GeocoderAddressBean(String address, String city, String state,
			String country, String postalCode, String knownName) {
		super();
		this.address = address;
		this.city = city;
		this.state = state;
		this.country = country;
		this.postalCode = postalCode;
		this.knownName = knownName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getKnownName() {
		return knownName;
	}

	public void setKnownName(String knownName) {
		this.knownName = knownName;
	}

}
