package ayAPI;

import java.util.Vector;
import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.EClientSocket;
import com.ib.client.EWrapper;
import com.ib.client.Execution;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.TagValue;
import com.ib.client.CommissionReport;
import com.ib.client.UnderComp;
import com.ib.controller.ApiController.ITimeHandler;

// RealTimeBars Class is an implementation of the 
// IB API EWrapper class

public class managerRealTimeData implements EWrapper
{
	private int nextOrderID = 0;// Keep track of the next ID
	private EClientSocket client = null;// The IB API Client Socket object
	
	private fiveSec[] arrFiveSec;//array to hold all 5-sec values.
	private int countResponseFromTws;
	private barByInterval barObj;
	private final int NUMBER_OF_RECORDS_BY_INTERVAL;
	private final int INTERVAL_GRAPH;
	private boolean isSync;
	//private long currentTime=0;

	public managerRealTimeData(String symbol, int intervalGraph)
	{
		this.isSync = false;
		this.INTERVAL_GRAPH = intervalGraph;
		this.NUMBER_OF_RECORDS_BY_INTERVAL =  intervalGraph*60/5;
		arrFiveSec = new fiveSec[this.NUMBER_OF_RECORDS_BY_INTERVAL];
		countResponseFromTws = 0;
		
		getBarsByInterval(symbol);
	} 

	public void getBarsByInterval(String symbol)
	{
		client = new EClientSocket (this);// Create a new EClientSocket object
		client.eConnect (null, 7496, 0);// Connect to the TWS or IB Gateway application

		
		try // Pause here for connection to complete
		{
			// Thread.sleep(1000);//one sec
			while (! (client.isConnected()));
		} catch (Exception e) 
		{
			e.printStackTrace ();
		}
		
		
		// Create a new contract
		Contract contract = new Contract ();
		contract.m_symbol = symbol;
		contract.m_exchange = "SMART";
		contract.m_secType = "STK";
		contract.m_currency = "USD";
			
		Vector<TagValue> realTimeBarsOptions = new Vector<TagValue>();// Create a TagValue list
		
		client.reqRealTimeBars(0, contract,5,"TRADES",false,realTimeBarsOptions);// will be returned via the realtimeBar method
	
	} 

	public void realtimeBar(int reqId, long time, double open, double high, double low, double close, long volume, double wap, int count)
	{
		if (!this.isSync)//sync to system time
		{
			long mili = time*1000L;
			int secondsStart = (int) (mili / 1000) % 60 ;
			int minutesStart = (int) ((mili / (1000*60)) % 60);
			
			if ((minutesStart % this.INTERVAL_GRAPH)==0)
			{
				if (secondsStart == 0)
				{
					this.isSync = true;
					System.out.println("is sync!");
					
				}
				else
					return;
			}
			else
				return;
	
		}
		
		fiveSec fiveSecObj;

		try
		{
			System.out.println("realtimeBar:" + time + "," + open + "," + high + "," + low + "," + close + ",volume: " +volume + ", counter:"+countResponseFromTws);
			
			fiveSecObj = new fiveSec(time,open,high,low,close,volume,countResponseFromTws);
			arrFiveSec[countResponseFromTws] = fiveSecObj;
			countResponseFromTws++;
			
			if (countResponseFromTws == NUMBER_OF_RECORDS_BY_INTERVAL)
			{
				
				barObj = new barByInterval(arrFiveSec, NUMBER_OF_RECORDS_BY_INTERVAL, time);//make the bar values
				
				System.err.println("barByInterval: barSize:"+this.INTERVAL_GRAPH+", high:"+ barObj.getHigh() +" , low: "+barObj.getLow()+" ,open: "+barObj.getOpen()+" , close: "+barObj.getClose()+" , vol: "+barObj.getVolume());
				System.err.println(barObj.convertToJSON());
				countResponseFromTws = 0;	
				//TODO-sent to socket-client
			}
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
	}
	public void currentTime(long time)
	{	
		long currentDateTime = time*1000L;
		
		System.out.println("corrent time: "+ currentDateTime);
		
	}
	
	
	
	
	
	
	
	
	
	
	//****************
	//unused function
	//****************
	public void bondContractDetails(int reqId, ContractDetails contractDetails)
	{
	}

	public void contractDetails(int reqId, ContractDetails contractDetails)
	{
	}

	public void contractDetailsEnd(int reqId)
	{
	}

	public void fundamentalData(int reqId, String data)
	{
	}

	public void bondContractDetails(ContractDetails contractDetails)
	{
	}

	public void contractDetails(ContractDetails contractDetails)
	{
	}

	

	public void displayGroupList(int requestId, String contraftInfo)
	{
	}


	public void displayGroupUpdated(int requestId, String contractInfo)
	{
	}

	public void verifyCompleted(boolean completed, String contractInfo)
	{
	}
	public void verifyMessageAPI(String message)
	{
	}

	public void execDetails(int orderId, Contract contract, Execution execution)
	{
	}

	public void execDetailsEnd(int reqId)
	{
	}

	public void historicalData(int reqId, String date, double open,
			double high, double low, double close, int volume, int count,
			double WAP, boolean hasGaps)
	{
	}

	public void managedAccounts(String accountsList)
	{
	}

	public void commissionReport(CommissionReport cr)
	{
	}

	public void position(String account, Contract contract, int pos, double avgCost)
	{
	}

	public void positionEnd()
	{
	}

	public void accountSummary(int reqId, String account, String tag, String value, String currency)
	{
	}

	public void accountSummaryEnd(int reqId)
	{
	}

	public void accountDownloadEnd(String accountName)
	{
	}

	public void openOrder(int orderId, Contract contract, Order order,
			OrderState orderState)
	{
	}

	public void openOrderEnd()
	{
	}


	public void orderStatus(int orderId, String status, int filled,
			int remaining, double avgFillPrice, int permId, int parentId,
			double lastFillPrice, int clientId, String whyHeld)
	{
	}

	public void receiveFA(int faDataType, String xml)
	{
	}

	public void scannerData(int reqId, int rank,
			ContractDetails contractDetails, String distance, String benchmark,
			String projection, String legsStr)
	{
	}

	public void scannerDataEnd(int reqId)
	{
	}

	public void scannerParameters(String xml)
	{
	}

	public void tickEFP(int symbolId, int tickType, double basisPoints,
			String formattedBasisPoints, double impliedFuture, int holdDays,
			String futureExpiry, double dividendImpact, double dividendsToExpiry)
	{
	}

	public void tickGeneric(int symbolId, int tickType, double value)
	{
	}

	public void tickOptionComputation( int tickerId, int field, 
			double impliedVol, double delta, double optPrice, 
			double pvDividend, double gamma, double vega, 
			double theta, double undPrice)
	{
	}


	public void deltaNeutralValidation(int reqId, UnderComp underComp) 
	{
	}


	public void updateAccountTime(String timeStamp)
	{
	}

	public void updateAccountValue(String key, String value, String currency,
			String accountName)
	{
	}

	public void updateMktDepth(int symbolId, int position, int operation,
			int side, double price, int size)
	{
	}

	public void updateMktDepthL2(int symbolId, int position,
			String marketMaker, int operation, int side, double price, int size)
	{
	}

	public void updateNewsBulletin(int msgId, int msgType, String message,
			String origExchange)
	{
	}

	public void updatePortfolio(Contract contract, int position,
			double marketPrice, double marketValue, double averageCost,
			double unrealizedPNL, double realizedPNL, String accountName)
	{
	}

	public void marketDataType(int reqId, int marketDataType)
	{
	}

	public void tickSnapshotEnd(int tickerId)
	{
	}

	public void connectionClosed()
	{
	}

	


	public void error(Exception e)
	{
		// Print out a stack trace for the exception
		e.printStackTrace ();
	}

	public void error(String str)
	{
		// Print out the error message
		System.err.println (str);
	}

	public void error(int id, int errorCode, String errorMsg)
	{
		// Overloaded error event (from IB) with their own error 
		// codes and messages
		System.err.println ("error: " + id + "," + errorCode + "," + errorMsg);
	}

	public void nextValidId (int orderId)
	{
		// Return the next valid OrderID
		nextOrderID = orderId;
	}


	public void tickPrice(int orderId, int field, double price,
			int canAutoExecute)
	{

	}

	public void tickSize (int orderId, int field, int size)
	{
	}

	public void tickString (int orderId, int tickType, String value)
	{
	}



} // end public class RealTimeBars