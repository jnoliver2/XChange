package org.known.xchange.dsx;

import java.io.InputStream;

import org.knowm.xchange.BaseExchange;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.utils.nonce.TimestampIncrementingNonceFactory;
import org.known.xchange.dsx.dto.marketdata.DSXExchangeInfo;
import org.known.xchange.dsx.dto.meta.DSXMetaData;
import org.known.xchange.dsx.service.DSXMarketDataService;

import si.mazi.rescu.SynchronizedValueFactory;

/**
 * @author Mikhail Wall
 */
public class DSXExchange extends BaseExchange implements Exchange {

  private SynchronizedValueFactory<Long> nonceFactory = new TimestampIncrementingNonceFactory();
  private DSXMetaData dsxMetaData;
  private DSXExchangeInfo dsxExchangeInfo;

  @Override
  protected void initServices() {
    this.marketDataService = new DSXMarketDataService(this);

  }

  @Override
  public SynchronizedValueFactory<Long> getNonceFactory() {

    return nonceFactory;
  }

  @Override
  protected void loadExchangeMetaData(InputStream is) {

    dsxMetaData = loadMetaData(is, DSXMetaData.class);
    exchangeMetaData = DSXAdapters.toMetaData(null, dsxMetaData);
  }

  @Override
  public ExchangeSpecification getDefaultExchangeSpecification() {

    ExchangeSpecification exchangeSpecification = new ExchangeSpecification(this.getClass().getCanonicalName());
    exchangeSpecification.setSslUri("https://dsx.uk");
    exchangeSpecification.setHost("dsx.uk");
    exchangeSpecification.setPort(80);
    exchangeSpecification.setExchangeName("DSX");
    exchangeSpecification.setExchangeDescription("DSX the UK first Digital Securities Exchange run by the FCA regulated ePayments.");

    return exchangeSpecification;
  }

  @Override
  public void remoteInit() {
    try {
      DSXMarketDataService marketDataService = (DSXMarketDataService) this.marketDataService;
      dsxExchangeInfo = marketDataService.getDSXInfo();
      exchangeMetaData = DSXAdapters.toMetaData(dsxExchangeInfo, dsxMetaData);
    } catch (Exception e) {
      logger.warn("An exception occured while loading the metadata");
    }
  }

  public DSXMetaData getDsxMetaData() {

    return dsxMetaData;
  }

  public DSXExchangeInfo getDsxExchangeInfo() {

    return dsxExchangeInfo;
  }
}
