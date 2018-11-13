package grakn.core.graql.internal.reasoner.graph;

import grakn.core.Session;
import grakn.core.Transaction;
import grakn.core.concept.Attribute;
import grakn.core.concept.AttributeType;
import grakn.core.concept.EntityType;
import grakn.core.concept.Label;
import grakn.core.concept.RelationshipType;
import grakn.core.concept.Role;
import grakn.core.concept.Thing;
import grakn.core.graql.Pattern;

public class GeoGraph {

    private Transaction tx;
    private final Session session;
    private AttributeType<String> key;

    private EntityType university, city, region, country, continent, geographicalObject;
    private RelationshipType isLocatedIn;

    private Role geoEntity, entityLocation;

    private Thing Europe;
    private Thing Warsaw, Wroclaw, London, Munich, Paris, Milan;
    private Thing Masovia, Silesia, GreaterLondon, Bavaria, IleDeFrance, Lombardy;
    private Thing Poland, England, Germany, France, Italy;
    private Thing UW;
    private Thing PW;
    private Thing Imperial;
    private Thing UCL;

    public GeoGraph(Session session){
        this.session = session;
    }

    public void load(){
        tx = session.transaction(Transaction.Type.WRITE);
        buildSchema();
        buildInstances();
        buildRelations();
        buildRules();
        tx.commit();
    }

    private void buildSchema() {
        key = tx.putAttributeType("name", AttributeType.DataType.STRING);

        geoEntity = tx.putRole("geo-entity");
        entityLocation = tx.putRole("entity-location");
        isLocatedIn = tx.putRelationshipType("is-located-in")
                .relates(geoEntity).relates(entityLocation);

        geographicalObject = tx.putEntityType("geoObject")
                .plays(geoEntity)
                .plays(entityLocation);
        geographicalObject.has(key);

        continent = tx.putEntityType("continent")
                .sup(geographicalObject)
                .plays(entityLocation);
        country = tx.putEntityType("country")
                .sup(geographicalObject)
                .plays(geoEntity)
                .plays(entityLocation);
        region = tx.putEntityType("region")
                .sup(geographicalObject)
                .plays(geoEntity)
                .plays(entityLocation);
        city = tx.putEntityType("city")
                .sup(geographicalObject)
                .plays(geoEntity)
                .plays(entityLocation);
        university = tx.putEntityType("university")
                .plays(geoEntity);
        university.has(key);
    }

    private void buildInstances() {
        Europe = putEntityWithResource(tx, "Europe", continent, key.label());

        Poland = putEntityWithResource(tx, "Poland", country, key.label());
        Masovia = putEntityWithResource(tx, "Masovia", region, key.label());
        Silesia = putEntityWithResource(tx, "Silesia", region, key.label());
        Warsaw = putEntityWithResource(tx, "Warsaw", city, key.label());
        Wroclaw = putEntityWithResource(tx, "Wroclaw", city, key.label());
        UW = putEntityWithResource(tx, "University-of-Warsaw", university, key.label());
        PW = putEntityWithResource(tx, "Warsaw-Polytechnics", university, key.label());

        England = putEntityWithResource(tx, "England", country, key.label());
        GreaterLondon = putEntityWithResource(tx, "GreaterLondon", region, key.label());
        London = putEntityWithResource(tx, "London", city, key.label());
        Imperial = putEntityWithResource(tx, "Imperial College London", university, key.label());
        UCL = putEntityWithResource(tx, "University College London", university, key.label());

        Germany = putEntityWithResource(tx, "Germany", country, key.label());
        Bavaria = putEntityWithResource(tx, "Bavaria", region, key.label());
        Munich = putEntityWithResource(tx, "Munich", city, key.label());
        putEntityWithResource(tx, "University of Munich", university, key.label());

        France = putEntityWithResource(tx, "France", country, key.label());
        IleDeFrance = putEntityWithResource(tx, "IleDeFrance", region, key.label());
        Paris = putEntityWithResource(tx, "Paris", city, key.label());

        Italy = putEntityWithResource(tx, "Italy", country, key.label());
        Lombardy = putEntityWithResource(tx, "Lombardy", region, key.label());
        Milan = putEntityWithResource(tx, "Milan", city, key.label());
    }

    private void buildRelations() {

        isLocatedIn.create()
                .assign(geoEntity, Poland)
                .assign(entityLocation, Europe);

        isLocatedIn.create()
                .assign(geoEntity, Masovia)
                .assign(entityLocation, Poland);

        isLocatedIn.create()
                .assign(geoEntity, Warsaw)
                .assign(entityLocation, Masovia);

        isLocatedIn.create()
                .assign(geoEntity, PW)
                .assign(entityLocation, Warsaw);

        isLocatedIn.create()
                .assign(geoEntity, UW)
                .assign(entityLocation, Warsaw);

        isLocatedIn.create()
                .assign(geoEntity, Silesia)
                .assign(entityLocation, Poland);

        isLocatedIn.create()
                .assign(geoEntity, Wroclaw)
                .assign(entityLocation, Silesia);



        isLocatedIn.create()
                .assign(geoEntity, Imperial)
                .assign(entityLocation, London);
        isLocatedIn.create()
                .assign(geoEntity, UCL)
                .assign(entityLocation, London);
        isLocatedIn.create()
                .assign(geoEntity, London)
                .assign(entityLocation, GreaterLondon);
        isLocatedIn.create()
                .assign(geoEntity, GreaterLondon)
                .assign(entityLocation, England);
        isLocatedIn.create()
                .assign(geoEntity, England)
                .assign(entityLocation, Europe);

        isLocatedIn.create()
                .assign(geoEntity, Munich)
                .assign(entityLocation, Bavaria);
        isLocatedIn.create()
                .assign(geoEntity, Bavaria)
                .assign(entityLocation, Germany);
        isLocatedIn.create()
                .assign(geoEntity, Germany)
                .assign(entityLocation, Europe);

        isLocatedIn.create()
                .assign(geoEntity, Milan)
                .assign(entityLocation, Lombardy);
        isLocatedIn.create()
                .assign(geoEntity, Lombardy)
                .assign(entityLocation, Italy);
        isLocatedIn.create()
                .assign(geoEntity, Italy)
                .assign(entityLocation, Europe);

        isLocatedIn.create()
                .assign(geoEntity, Paris)
                .assign(entityLocation, IleDeFrance);
        isLocatedIn.create()
                .assign(geoEntity, IleDeFrance)
                .assign(entityLocation, France);
        isLocatedIn.create()
                .assign(geoEntity, France)
                .assign(entityLocation, Europe);
    }

    private void buildRules() {
        Pattern transitivity_LHS = tx.graql().parser().parsePattern("{(geo-entity: $x, entity-location: $y) isa is-located-in;" +
                "(geo-entity: $y, entity-location: $z) isa is-located-in;}");
        Pattern transitivity_RHS = tx.graql().parser().parsePattern("{(geo-entity: $x, entity-location: $z) isa is-located-in;}");
        tx.putRule("Geo Rule", transitivity_LHS, transitivity_RHS);
    }

    private Thing putEntityWithResource(Transaction tx, String id, EntityType type, Label key) {
        Thing inst = type.create();
        putResource(inst, tx.getSchemaConcept(key), id);
        return inst;
    }

    private <T> void putResource(Thing thing, AttributeType<T> attributeType, T resource) {
        Attribute attributeInstance = attributeType.create(resource);
        thing.has(attributeInstance);
    }
}