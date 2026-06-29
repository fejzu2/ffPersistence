package pl.fejzu.persistence.mongodb.codec;

import dev.morphia.Datastore;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import com.mongodb.MongoClientSettings;

public final class MongoCodecConfig {

    private final Datastore datastore;

    public MongoCodecConfig(Datastore datastore) {
        this.datastore = datastore;
    }

    public CodecRegistry buildRegistry() {
        return fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );
    }
}
