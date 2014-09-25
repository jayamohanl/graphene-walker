package graphene.walker.model.graphserver;

import graphene.dao.EntityRefDAO;
import graphene.dao.IdTypeDAO;
import graphene.dao.TransactionDAO;
import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_CanonicalRelationshipType;
import graphene.model.idl.G_EdgeType;
import graphene.model.idl.G_IdType;
import graphene.model.query.StringQuery;
import graphene.services.EventGraphBuilder;
import graphene.util.validator.ValidationUtils;
import graphene.walker.model.sql.walker.WalkerIdentifierType100;
import graphene.walker.model.sql.walker.WalkerTransactionPair100;

import java.util.Iterator;

import mil.darpa.vande.generic.V_GenericEdge;
import mil.darpa.vande.generic.V_GenericNode;
import mil.darpa.vande.generic.V_GraphQuery;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

/**
 * This replaces the finder/builder implementations, which were developed for
 * interaction graphs.
 * 
 * @author djue
 * 
 */
public class EventGraphBuilderWalkerImpl extends
		EventGraphBuilder<WalkerTransactionPair100> {

	private IdTypeDAO<WalkerIdentifierType100, StringQuery> idTypeDAO;

	@Inject
	Logger logger;

	private EntityRefDAO propertyDAO;

	@Inject
	public EventGraphBuilderWalkerImpl(IdTypeDAO idTypeDAO, TransactionDAO dao,
			EntityRefDAO propertyDAO) {
		super();
		this.idTypeDAO = idTypeDAO;
		this.dao = dao;
		this.propertyDAO = propertyDAO;
		this.supportedDatasets.add("Walker");
		this.supportedDatasets.add("events");
	}

	/**
	 * This callback just creates the nodes and edges from a single row.
	 */
	@Override
	public boolean callBack(WalkerTransactionPair100 p) {

		String s_acno = p.getSenderId().toString();
		String s_acname = p.getSenderValueStr();

		String t_acno = p.getReceiverId().toString();
		String t_acname = p.getReceiverValueStr();

		V_GenericNode src = null, target = null;
		G_IdType commonNodeType;
		try {
			commonNodeType = nodeTypeAccess
					.getCommonNodeType(G_CanonicalPropertyType.ACCOUNT);

			if (ValidationUtils.isValid(s_acno)) {
				src = nodeList.getNode(s_acno);
				if (src == null) {
					// #F08080 is coral
					// #90EE90 is pale green
					// #22FF22 is vibrant green

					src = new V_GenericNode(s_acno);
					src.setIdType("account");
					src.setNodeType(commonNodeType.getName());
					src.setIdVal(s_acno);
					src.setValue(s_acno);
					src.setLabel(s_acname);
					src.setColor("#22FF22"); // "#F08080" is coral

					unscannedNodeList.add(src);
					nodeList.addNode(src);
				}

			}
			if (ValidationUtils.isValid(t_acno)) {
				target = nodeList.getNode(t_acno);
				if (target == null) {
					target = new V_GenericNode(t_acno);
					target.setIdType("account");

					target.setNodeType(commonNodeType.getName());
					target.setIdVal(t_acno);
					target.setValue(t_acno);
					target.setLabel(t_acname);
					target.setColor("#22FF22");
					// value type is "customer"
					// target.addProperty("Account Number", t_acno);
					// target.addProperty("Account Owner", t_acname);
					// target.addProperty("color", "red");

					unscannedNodeList.add(target);
					nodeList.addNode(target);
				}

			}

			if (src != null && target != null) {
				// Here, an event id is used, so we will get an edge per event.
				String key = generateEdgeId(p.getPairId().toString());

				if (key != null && !edgeMap.containsKey(key)) {
					G_EdgeType edgeType;
					try {
						edgeType = edgeTypeAccess
								.getCommonEdgeType(G_CanonicalRelationshipType.OWNER_OF);

						V_GenericEdge v = new V_GenericEdge(src, target);
						v.setIdType(edgeType.getName());
						// v.setLabel(G_CanonicalRelationshipType.OWNER_OF.name());
						String subject = p.getTrnSubjStr();
						String payload = p.getTrnValueStr();
						String label = subject;

						// prune all but 1 "RE:" if it is present
						int index = subject.lastIndexOf("RE:");
						if (index > 0) { // i.e. index is not -1 or 0
							label = subject.substring(index);
						}

						if (label.length() > 15) {
							label = label.substring(0, 15) + "...";
						}

						v.setLabel(label);
						v.setIdVal(edgeType.getName());
						long dt = p.getTrnDt().getTime();
						double value = p.getTrnValueNbr();
						v.setDoubleValue(value);

						v.addData("date", Long.toString(dt));
						v.addData("amount", Double.toString(value));
						v.addData("id", p.getPairId().toString());
						v.addData("payload", payload);
						v.addData("subject", subject);
						edgeMap.put(key, v);
					} catch (AvroRemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					// Handle how multiple edges are aggregated.
				}

			}
		} catch (AvroRemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return true;
	}

	@Override
	public void performPostProcess(V_GraphQuery graphQuery) {
		Iterator<String> iter = graphQuery.getSearchIds().iterator();

		logger.debug("Performing post process");
		String color = "red";

		while (iter.hasNext()) {
			String id = iter.next();
			try {
				V_GenericNode searched = nodeList.getNode(id);
				searched.setColor(color);
				logger.debug("Node with id(" + id + ") is now " + color);
			} catch (Exception e) {
				logger.error("Could not find node with id = " + id);
			}
		}
	}
}
