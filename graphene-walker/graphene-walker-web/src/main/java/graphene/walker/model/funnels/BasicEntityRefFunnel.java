package graphene.walker.model.funnels;

import graphene.walker.model.sql.walker.WalkerEntityref100;
import graphene.model.Funnel;
import graphene.model.view.entities.BasicEntityRef;

import org.apache.commons.lang3.time.DateFormatUtils;

public class BasicEntityRefFunnel implements
		Funnel<BasicEntityRef, WalkerEntityref100> {

	@Override
	public BasicEntityRef to(WalkerEntityref100 f) {
		BasicEntityRef b = new BasicEntityRef();
		b.setAccountNumber(f.getAccountnumber());
		b.setAccountType(f.getAccounttype());
		b.setCustomerNumber(f.getCustomernumber());
		b.setCustomerType(f.getCustomertype());

		b.setDateStart(f.getDatestart() != null ? DateFormatUtils.ISO_DATE_FORMAT
				.format(f.getDatestart()) : null);
		b.setDateEnd(f.getDateend() != null ? DateFormatUtils.ISO_DATE_FORMAT
				.format(f.getDateend()) : null);

		b.setEntityrefId(f.getEntityrefId());

		b.setIdentifier(f.getIdentifier());

		b.setIdentifierColumnSource(f.getIdentifiercolumnsource());
		b.setIdentifierTableSource(f.getIdentifiertablesource());
		b.setIdtypeId(f.getIdtypeId());

		return b;
	}

	@Override
	public WalkerEntityref100 from(BasicEntityRef f) {
		// TODO Auto-generated method stub
		return null;
	}

	//@Override
	//public BasicEntityRef to(WalkerEntityref100 f) {
	//	// TODO Auto-generated method stub
	//	return null;
	//}
}
