package server.dba;

import java.util.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import shared.comm.Result;
import shared.comm.Search_Params;
import shared.comm.Search_Results;
import shared.comm.Submit_Params;
import shared.model.Record;

/**
 * Used to access the Records table. 
 * @author jageorge
 *
 */
public class RecordsDba {
	private Database db;
	
	public RecordsDba(Database database) {
		db = database;
	}

	public Result submitBatch(Submit_Params params){
		Result ret = new Result(false);
		if(!db.getUsers().checkUser(params.getUsername(), params.getPassword()))
			return ret;

		
		try {
			
			//make sure that user has checked out the given batch else FAILED
			int batchId = db.getBatches().findUsersBatch(params.getUsername());
			if(batchId != params.getBatchId()){
				return ret;
			}
			
			//Add the values to the database
			List<Integer> fieldIds = db.getFields().getFieldIds(params.getBatchId());
			String[] rows = params.getFieldValues().split(";");
			int indexedRecords =0;
			for(int r=0; r<rows.length; r++){
				String[] values = rows[r].split(",");
				indexedRecords++;
				for (int f=0; f<values.length; f++){
					Record newRecord = 
							new Record(params.getBatchId(),fieldIds.get(f), r, values[f].trim());
					addRecord(newRecord);
				}
			}
			
			//Update the user's num of indexed records
			db.getUsers().updateIndexedRecords(params.getUsername(),indexedRecords);
			//Update the user's current batch
			db.getBatches().SetBatchUser("null", params.getBatchId());
			//set the batch to indexed
			db.getBatches().setIndexed(params.getBatchId());
			
			ret.setSuccess(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	
	public Search_Results search(Search_Params params){
		
		Search_Results ret = new Search_Results();
		
		if(!db.getUsers().checkUser(params.getUsername(), params.getPassword()))
			return ret; //if user isn't valid, FAIL
		

		if( params.getFields().equals("") || params.getSearchValues().equals(""))
			return ret; //if missing fields or values, FAIL
		
		String[] fieldIds =params.getFields().split("\\s*,\\s*");
		String[] searchValues = params.getSearchValues().split(",");
		
		if(!db.getFields().checkFieldIds(fieldIds))
			return ret; //if fields aren't valid, FAIL
			
		//Build the SQL string based on params
		String sql = generateSearchSQLString(fieldIds, searchValues);
		
		//run the Query and get the results
		ResultSet rs = null;
		try(PreparedStatement stmt = db.getConnection().prepareStatement(sql);) {
			rs = stmt.executeQuery();
			while(rs.next()){
				int batchId = rs.getInt(1);
				int fieldId = rs.getInt(2);
				int recordNum = rs.getInt(3);
				String imageURL = db.getBatches().getBatchURL(batchId);
				ret.addresult(batchId, imageURL, recordNum, fieldId);
			}
			ret.setSuccess(true);
		}catch (SQLException e){
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Generates the search SQL string based on the parameters of the search
	 * @param params
	 * @return
	 */
	private String generateSearchSQLString(String[] fieldIds,
			String[] values) {
		StringBuilder sql = new StringBuilder(
						"SELECT BatchId, FieldId, recordNum" +
						" FROM Records" + 
						" WHERE FieldId IN ('");
		
		for(int i=0; i<fieldIds.length; i++){
			if(i >0)
				sql.append("' , '");
			sql.append(fieldIds[i].trim());
		}
		sql.append("') AND Value IN ('");
		
		for(int i=0; i<values.length; i++){
			if(i >0)
				sql.append("' , '");
			sql.append(values[i].toUpperCase().trim());
		}
		sql.append("');");
		return sql.toString();
	}

	public void addRecord(Record record) {
		String sql = "INSERT INTO Records" + 
	   			 "(BatchId,FieldId,RecordNum,Value)" +
	                "values(?,?,?,?)";
		try(PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
		    stmt.setInt(1, record.getBatchId());
		    stmt.setInt(2, record.getFieldId());
		    stmt.setInt(3, record.getRecordNum());
		    stmt.setString(4, record.getValue().toUpperCase().trim()); //make upper case and trim.
		    if (stmt.executeUpdate() != 1)
		    	;//TODO Throw error?
		}catch(SQLException e){
			System.err.println("SQL exception thrown");
			e.printStackTrace();
		}
	}
	
}
