package com.fusui.tapir.common.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;


/*
 * @author gko
 */

// GKO TODO
// simple POJO, may need to add more functions and data types
@XmlRootElement
public class VoTable implements Serializable {
	
	public static final int CELL_TYPE_INVISIBLE = -1;
	public static final int CELL_TYPE_STRING = 0;
	public static final String CELL_NO_PERMISSION = null;
	
	@XmlRootElement
	public static class VoTables  implements Serializable {
		private Map<String, VoTable> tables;

		public VoTables() {
		}
		
		public Map<String, VoTable> getTables() {
			return tables;
		}

		public void setTables(Map<String, VoTable> tables) {
			this.tables = tables;
		}
	}
	
	
	@XmlRootElement
	public static class VoRow implements Serializable {
		private List <VoCell> cells;
		
		public VoRow() {
		}

		public List<VoCell> getCells() {
			return cells;
		}

		public void setCells(List<VoCell> cells) {
			this.cells = cells;
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			int size = cells.size();
			boolean hasTab = false;
			for (int i=0; i<size; i++) {
				 
				
				VoCell cell = cells.get(i);
				String value = cell.getValue();
				//sb.append(value);
				
				hasTab =  i != size-1;
				if (hasTab) {
					// GKO TODO items-detail-main-item_info
					if (value != null && value.equals("\r")) {
						value = "\n";
						hasTab = false;
					}
					else {
						// check next cell
						String nextValue = cells.get(i+1).getValue();
						if (nextValue != null && nextValue.equals("\r")) {
							hasTab = false;
						}
					}
				}
				
				sb.append(value);

				if (hasTab) {
					sb.append("\t");
				}
				
			}
			sb.append('\n');
			return sb.toString();
		}
	}
	
	@XmlRootElement
	public static class VoCell implements Serializable {
		private String value;
		private int dt;
		
		public VoCell () {
		}

		
		public VoCell (String value, int dt) {
			if (value == null) {
				value = "";
			}
			this.value = value;
			this.dt = dt;
		}
		
		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			if (value == null) {
				value = "";
			}
			this.value = value;
		}

		public int getDt() {
			return dt;
		}

		public void setDt(int dt) {
			this.dt = dt;
		}
	}

	private List <String> columnNames;
	private List <String> columnLabels;
	private List <VoRow> rows;
	
	public VoTable() {
	}
	
	public List<String> getColumnNames() {
		return columnNames;
	}
	public void setColumnNames(List<String> names) {
		this.columnNames = names;
	}
	public List<String> getColumnLabels() {
		return columnLabels;
	}
	public void setColumnLabels(List<String> labels) {
		this.columnLabels = labels;
	}
	public List<VoRow> getRows() {
		return rows;
	}
	public void setRows(List<VoRow> rows) {
		this.rows = rows;
	}
	
	public void addRow(VoRow row) {
		if (rows == null) {
			rows = new ArrayList <VoRow>();
		}
		rows.add(row);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		//sb.append("columns\n");
		if (columnNames != null) {
			for (int i=0; i<columnNames.size(); i++) {
				if (i!=0) {
					sb.append("\t");
				}
				sb.append(columnNames.get(i));
				sb.append("(");
				sb.append(columnLabels.get(i));
				sb.append(")");

			}
			sb.append("\n");
		}
		
		if (rows != null) {
			for (int i=0; i<rows.size(); i++) {
				sb.append (rows.get(i).toString());
			}
		}
		
		//sb.append("--------------------");
		
		return sb.toString();
	}
}
