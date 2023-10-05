package helpers;

import org.aeonbits.owner.ConfigFactory;

/**
 * Класс предоставляющий доступа к различным свойствам, представленным в {@link TestProperties}.
 *
 * @author Юрий Юрченко
 * @see TestProperties
 */
public class Properties {

    public static TestProperties testProperties = ConfigFactory.create(TestProperties.class);
}
