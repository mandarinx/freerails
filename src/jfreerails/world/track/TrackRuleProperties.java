package jfreerails.world.track;

import jfreerails.world.common.FreerailsSerializable;

final public class TrackRuleProperties implements FreerailsSerializable {

	public String getTypeName() {
		return typeName;
	}

	public boolean isDoubleTrackEnabled() {
		return false;
	}
	public int getRuleNumber(){
		return number;
	}
	public boolean isStation(){
		return isStation;
	}
	
	private final int rGBvalue;
	private final int number;		//This rule's position in the track rule list.

	private final boolean enableDoubleTrack;
	private final String typeName;
	private final boolean isStation;
	
	
	public	TrackRuleProperties(int rgb, boolean doubleTrack, String name, int n, boolean station){
		rGBvalue=rgb;
		enableDoubleTrack=doubleTrack;
		typeName=name;
		number=n;
		isStation=station;		
	}
	public boolean equals(Object o) {
		if(o instanceof TrackRuleProperties){
			TrackRuleProperties trackRuleProperties = (TrackRuleProperties)o;
			if(rGBvalue == trackRuleProperties.getRGBvalue()
			&& number == trackRuleProperties.getNumber()
			&& enableDoubleTrack == trackRuleProperties.isEnableDoubleTrack()
			&& typeName.equals(trackRuleProperties.getTypeName())
			&& isStation == trackRuleProperties.isStation()	){
				return true;
			}else{
				return false;
			}			
		}else{
			return false;
		}
	}

	public boolean isEnableDoubleTrack() {
		return enableDoubleTrack;
	}

	public int getNumber() {
		return number;
	}

	public int getRGBvalue() {
		return rGBvalue;
	}

}