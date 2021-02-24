package beans;

public class GetWorkOrderDetailsBean {
	
	private String ad_code,ad_work_order_date,ad_work_order_no,ad_work_order_serial_code,distcode,distname;

	public GetWorkOrderDetailsBean(String ad_code, String ad_work_order_date,
			 String ad_work_order_serial_code,
			String distcode, String distname) {
		super();
		this.ad_code = ad_code;
		this.ad_work_order_date = ad_work_order_date;
		this.ad_work_order_serial_code = ad_work_order_serial_code;
		this.distcode = distcode;
		this.distname = distname;
	}

	public String getDistcode() {
		return distcode;
	}

	public void setDistcode(String distcode) {
		this.distcode = distcode;
	}

	public String getDistname() {
		return distname;
	}

	public void setDistname(String distname) {
		this.distname = distname;
	}


	public String getAd_work_order_serial_code() {
		return ad_work_order_serial_code;
	}

	public void setAd_work_order_serial_code(String ad_work_order_serial_code) {
		this.ad_work_order_serial_code = ad_work_order_serial_code;
	}

	
	public String getAd_code() {
		return ad_code;
	}

	public void setAd_code(String ad_code) {
		this.ad_code = ad_code;
	}

	public String getAd_work_order_date() {
		return ad_work_order_date;
	}

	public void setAd_work_order_date(String ad_work_order_date) {
		this.ad_work_order_date = ad_work_order_date;
	}

	public String getAd_work_order_no() {
		return ad_work_order_no;
	}

	public void setAd_work_order_no(String ad_work_order_no) {
		this.ad_work_order_no = ad_work_order_no;
	}

}
