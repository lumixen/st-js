package org.stjs.examples.stock;

import org.stjs.javascript.jquery.SuccessListener;

public class QuoteProviderStub implements QuoteProvider {

	@Override
	public void updateStock(Object stock, SuccessListener listener) {
		StockApplication.Quote quote = new StockApplication.Quote();
		quote.symbol = stock.toString();
		quote.LastTradePriceOnly = "3.10";
		quote.PreviousClose = "3.00";
		StockApplication.Response response = new StockApplication.Response();
		response.query = new StockApplication.Query();
		response.query.results = new StockApplication.Results();
		response.query.results.quote = quote;
		listener.$invoke(response);
	}
}
