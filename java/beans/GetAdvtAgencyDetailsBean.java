package beans;

public class GetAdvtAgencyDetailsBean {

	private String ad_agency_name, ad_authorised_email_id,
	ad_authorised_mobile_no, ad_authorised_person;

	public GetAdvtAgencyDetailsBean(String ad_agency_name,
			String ad_authorised_email_id, String ad_authorised_mobile_no,
			String ad_authorised_person) {
		super();
		this.ad_agency_name = ad_agency_name;
		this.ad_authorised_email_id = ad_authorised_email_id;
		this.ad_authorised_mobile_no = ad_authorised_mobile_no;
		this.ad_authorised_person = ad_authorised_person;
	}

	public String getAd_agency_name() {
		return ad_agency_name;
	}

	public void setAd_agency_name(String ad_agency_name) {
		this.ad_agency_name = ad_agency_name;
	}

	public String getAd_authorised_email_id() {
		return ad_authorised_email_id;
	}

	public void setAd_authorised_email_id(String ad_authorised_email_id) {
		this.ad_authorised_email_id = ad_authorised_email_id;
	}

	public String getAd_authorised_mobile_no() {
		return ad_authorised_mobile_no;
	}

	public void setAd_authorised_mobile_no(String ad_authorised_mobile_no) {
		this.ad_authorised_mobile_no = ad_authorised_mobile_no;
	}

	public String getAd_authorised_person() {
		return ad_authorised_person;
	}

	public void setAd_authorised_person(String ad_authorised_person) {
		this.ad_authorised_person = ad_authorised_person;
	}
	
}
