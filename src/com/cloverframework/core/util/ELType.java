package com.cloverframework.core.util;

public interface ELType {
	/**并集 */
	public static final String U = "U";
	/**交集 */
	public static final String I = "I";
	/**补集*/
	public static final String C = "C";
	/**前置并集 */
	public static final String UB = "UB";
	/**后置并集 */
	public static final String UA = "UA";
	/**前置混合 */
	public static final String MB = "MB";
	/**后置混合 */
	public static final String MA = "MA";
	/**正交 */
	public static final String M = "M";
	/**反交 */
	public static final String RM = "RM";
	/**左补 */
	public static final String CB = "CB";
	/**右补 */
	public static final String CA = "CA";//
	
	
	public static final String[] Model = {U,I,C,UB,UA,MB,MA,M,RM,CB,CA};
}
