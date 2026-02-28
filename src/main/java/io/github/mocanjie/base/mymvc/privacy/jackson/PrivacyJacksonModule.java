package io.github.mocanjie.base.mymvc.privacy.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import io.github.mocanjie.base.mymvc.privacy.Privacy;
import io.github.mocanjie.base.mymvc.privacy.PrivacyMaskUtils;

import java.util.List;

public class PrivacyJacksonModule extends SimpleModule {

    public PrivacyJacksonModule() {
        super("PrivacyJacksonModule");
    }

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        context.addBeanSerializerModifier(new PrivacyBeanSerializerModifier());
    }

    static class PrivacyBeanSerializerModifier extends BeanSerializerModifier {

        @Override
        public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                         BeanDescription beanDesc,
                                                         List<BeanPropertyWriter> beanProperties) {
            for (int i = 0; i < beanProperties.size(); i++) {
                BeanPropertyWriter writer = beanProperties.get(i);
                Privacy privacy = writer.getAnnotation(Privacy.class);
                if (privacy != null) {
                    beanProperties.set(i, new PrivacyPropertyWriter(writer, privacy));
                }
            }
            return beanProperties;
        }
    }

    static class PrivacyPropertyWriter extends BeanPropertyWriter {

        private final Privacy privacy;

        protected PrivacyPropertyWriter(BeanPropertyWriter base, Privacy privacy) {
            super(base);
            this.privacy = privacy;
        }

        @Override
        public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
            Object value = get(bean);
            if (value instanceof String str) {
                String masked = PrivacyMaskUtils.mask(str, privacy);
                gen.writeStringField(getName(), masked);
            } else {
                super.serializeAsField(bean, gen, prov);
            }
        }
    }
}
