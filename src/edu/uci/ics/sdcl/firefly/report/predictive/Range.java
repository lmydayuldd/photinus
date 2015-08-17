package edu.uci.ics.sdcl.firefly.report.predictive;

/**
 * Internal package class to hold the values of a filter 
 * @author adrianoc
 */
class Range {
	
		Integer max=-1;
		Integer min=-1;
		Double maxD=-1.0;
		Double minD=-1.0;
		int[] list;
		String[] professionExclusionList;

		public Range(int minValue, int maxValue){
			this.min = new Integer(minValue);
			this.max =  new Integer(maxValue);
		}

		public Range(double minValue, double maxValue){
			this.minD = new Double(minValue);
			this.maxD =  new Double(maxValue);
		}
		
		public Range(int[] workerExclusionList) {
			this.list = workerExclusionList.clone();
		}

		public Range(String[] professionList){
			this.professionExclusionList = professionList.clone();
		}

		public String toString(){
			if(list!=null && list.length>0)
				return "[excluded" +listToString(list)+  "]";
			else
				if(professionExclusionList!=null && professionExclusionList.length>0) 
					return "[excluded" +listToString(professionExclusionList)+  "]";
				else
					if(minD!=-1.0 || maxD!=-1.0)
						return "["+minD.toString()+","+maxD.toString()+"]";
					else
						return "["+min.toString()+","+max.toString()+"]";
		}

		private String listToString(String[] list){
			String result ="";
			for(String value: list){
				result = result + ","+value;
			}
			result = result.substring(0, result.length());
			return result;
		}

		private String listToString(int[] list){
			String result ="";
			for(int value: list){
				//System.out.println("value"+value);
				String valueStr = new Integer(value).toString();
				result = result + ","+valueStr;
			}
			result = result.substring(0, result.length());
			return result;
		}
	}

