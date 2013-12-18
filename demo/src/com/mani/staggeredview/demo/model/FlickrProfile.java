package com.mani.staggeredview.demo.model;

public class FlickrProfile {

	int iconfarm;
	
	int iconserver;
	
	String nsid;

	String path_alias;
	
	FlickrProfileRealName realname;
	
	FlickrProfileRealName username;
	
	public int getIconfarm() {
		return iconfarm;
	}

	public void setIconfarm(int iconfarm) {
		this.iconfarm = iconfarm;
	}

	public int getIconserver() {
		return iconserver;
	}

	public void setIconserver(int iconserver) {
		this.iconserver = iconserver;
	}

	public String getNsid() {
		return nsid;
	}

	public void setNsid(String nsid) {
		this.nsid = nsid;
	}
	
	public String getPath_alias() {
		return path_alias;
	}

	public void setPath_alias(String path_alias) {
		this.path_alias = path_alias;
	}

	public String getProfileName() {
		String profileName = null;
		if(username.get_content() != null ) {
			if(username.get_content().length() > 0) {
				profileName =  username.get_content();
			} else {
				profileName = realname.get_content();
			}
		} else {
			profileName = realname.get_content();
		}
		return profileName;
	}
	
	
	public FlickrProfileRealName getUsername() {
		return username;
	}

	public void setUsername(FlickrProfileRealName username) {
		this.username = username;
	}

	public FlickrProfileRealName getRealname() {
		return realname;
	}

	public void setRealname(FlickrProfileRealName realname) {
		this.realname = realname;
	}

	public String getProfileImageUrl() {
		return "http://farm"+iconfarm+".staticflickr.com/"+iconserver+"/buddyicons/"+nsid+".jpg";
	}
}
