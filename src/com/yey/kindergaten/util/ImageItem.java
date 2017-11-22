/**
 * 
 */
package com.yey.kindergaten.util;

import java.io.Serializable;

/**
 * 相片实体类
 * @author chaowen
 *
 */

	public class ImageItem implements Serializable {
		public String imageId;
		public String thumbnailPath;
		public String imagePath;
		public boolean isSelected = false;
	}

