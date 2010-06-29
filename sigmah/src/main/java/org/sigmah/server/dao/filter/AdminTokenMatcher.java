package org.sigmah.server.dao.filter;

import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.sigmah.server.domain.AdminEntity;
import org.sigmah.server.domain.Location;

import java.util.Locale;

/**
* @author Alex Bertram (akbertram@gmail.com)
*/
public class AdminTokenMatcher extends AbstractEntityTokenMatcher {

    public AdminTokenMatcher(Session session, Locale locale) {
        super(session, AdminEntity.class, locale);
    }

    @Override
    protected Criterion createCriterion(String name) {

        DetachedCriteria locations = DetachedCriteria.forClass(Location.class);
        locations.createAlias("adminEntities", "entity");
        locations.add( Restrictions.eq("entity.name", name ));
        locations.setProjection( Projections.property("id"));

        return Subqueries.propertyIn("location.id", locations);
    }

}