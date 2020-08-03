package alternative;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@Alternative
@Priority(1)
public class AlternativeProducers {

}
