package graphene.walker.model.funnels;

import graphene.model.Funnel;
import graphene.model.idl.G_Entity;
import graphene.model.idl.G_Property;
import graphene.model.idl.G_PropertyTag;
import graphene.model.idlhelper.EntityHelper;
import graphene.model.idlhelper.PropertyHelper;
import graphene.model.view.entities.EntityAttribute;
import graphene.model.view.entities.EntityLight;

import java.util.List;

public class WalkerEntityLightFunnel implements Funnel<EntityLight, G_Entity>{
	@Override
	public G_Entity from(EntityLight e) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public EntityLight to(G_Entity e) {
		EntityLight el = new EntityLight();
		el.setId(e.getUid());
		el.setDatasource_id(e.getProvenance().getUri());
		StringBuffer sb = new StringBuffer("");
		List<G_Property> propertiesByTag = EntityHelper.getPropertiesByTag(e, G_PropertyTag.NAME);
		for (G_Property p : propertiesByTag) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			// get the value of the property
			String n = (String) PropertyHelper.from(p).getValue();
			el.getAttributes().add(new EntityAttribute("Name", "", n));
			sb.append(n);
		}
		el.setEffectiveName(e.getProvenance().getUri() + " " + e.getUid() + ": " + sb.toString());
		if (propertiesByTag.size() > 1) {
			el.setEffectiveName(el.getEffectiveName() 
					+ " (" + propertiesByTag.size() + " associated values)");
		}
		el.setAllNames(sb.toString());
		for (G_Property p : EntityHelper.getPropertiesByKey(e, "phone")) {
			el.getAttributes().add(
					new EntityAttribute("Phone", p.getKey(),
							(String) PropertyHelper.from(p).getValue()));
		}
		for (G_Property p : EntityHelper.getPropertiesByTag(e, G_PropertyTag.GEO)) {
			el.getAttributes().add(
					new EntityAttribute("Address", p.getKey(),
							(String) PropertyHelper.from(p).getValue()));
		}
		for (G_Property p : EntityHelper.getPropertiesByKey(e, "email")) {
			el.getAttributes().add(
					new EntityAttribute("Email", p.getKey(),
							(String) PropertyHelper.from(p).getValue()));
		}
		for (G_Property p : EntityHelper.getPropertiesByTag(e, G_PropertyTag.ID)) {
			el.getAttributes().add(
					new EntityAttribute("Identifier", (String) PropertyHelper
							.from(p).getValue(), (String) PropertyHelper
							.from(p).getValue()));
		}
		for (G_Property ac : EntityHelper.getPropertiesByKey(e, "account")) {
			el.getAccountList().add((String) PropertyHelper.from(ac).getValue());
		}
		return el;

	}
}
