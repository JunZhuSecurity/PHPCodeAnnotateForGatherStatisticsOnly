package edu.uncc.aside.phpcodeannotate.models;

public class NodePositionInfo{
	private String fileDir;   //local full path in the project directory, e.g. /TestProject/testFolder/test.php
	private int startPosition;
    private int length;
    
    public NodePositionInfo(String fileDir, int startPosition, int length){
    	this.fileDir = fileDir;
    	this.startPosition = startPosition;
    	this.length = length;
    }
    
	public String getFileDir() {
		return fileDir;
	}
	public void setFileDir(String fileDir) {
		this.fileDir = fileDir;
	}
    
    public int getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileDir == null) ? 0 : fileDir.hashCode());
		result = prime * result + length;
		result = prime * result + startPosition;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodePositionInfo other = (NodePositionInfo) obj;
		if (fileDir == null) {
			if (other.fileDir != null)
				return false;
		} else if (!fileDir.equals(other.fileDir))
			return false;
		if (length != other.length)
			return false;
		if (startPosition != other.startPosition)
			return false;
		return true;
	}
	
	

}
