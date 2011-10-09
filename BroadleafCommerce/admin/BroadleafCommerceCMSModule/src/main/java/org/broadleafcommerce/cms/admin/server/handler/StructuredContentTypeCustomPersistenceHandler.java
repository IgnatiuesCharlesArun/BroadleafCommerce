package org.broadleafcommerce.cms.admin.server.handler;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.field.domain.FieldDefinition;
import org.broadleafcommerce.cms.field.domain.FieldEnumerationItem;
import org.broadleafcommerce.cms.field.domain.FieldGroup;
import org.broadleafcommerce.cms.structure.domain.*;
import org.broadleafcommerce.cms.structure.service.StructuredContentService;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.client.presentation.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.service.SandBoxContext;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.SandBoxService;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by jfischer
 */
public class StructuredContentTypeCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private Log LOG = LogFactory.getLog(StructuredContentTypeCustomPersistenceHandler.class);

    @Resource(name="blStructuredContentService")
	protected StructuredContentService structuredContentService;

    @Resource(name="blSandBoxService")
    protected SandBoxService sandBoxService;

    @Override
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        return
            StructuredContentType.class.getName().equals(ceilingEntityFullyQualifiedClassname) &&
            persistencePackage.getCustomCriteria() != null &&
            persistencePackage.getCustomCriteria().length > 0 &&
            persistencePackage.getCustomCriteria()[0].equals("constructForm");
    }

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        return false;
    }

    @Override
    public Boolean canHandleInspect(PersistencePackage persistencePackage) {
        return canHandleFetch(persistencePackage);
    }

    @Override
    public Boolean canHandleRemove(PersistencePackage persistencePackage) {
        return false;
    }

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        return canHandleFetch(persistencePackage);
    }

    protected SandBox getSandBox() {
        return sandBoxService.retrieveSandboxById(SandBoxContext.getSandBoxContext().getSandBoxId());
    }

    @Override
    public DynamicResultSet inspect(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            String structuredContentTypeId = persistencePackage.getCustomCriteria()[1];
            StructuredContentType structuredContentType = structuredContentService.findStructuredContentTypeById(Long.valueOf(structuredContentTypeId));
            ClassMetadata metadata = new ClassMetadata();
            metadata.setCeilingType(StructuredContentType.class.getName());
            PolymorphicEntity entity = new PolymorphicEntity();
            entity.setName("StructuredContentTypeImpl");
            entity.setType(StructuredContentTypeImpl.class.getName());
            PolymorphicEntity[] entities = new PolymorphicEntity[]{entity};
            metadata.setPolymorphicEntities(entities);
            int groupCount = 1;
            int fieldCount = 0;
            List<Property> propertiesList = new ArrayList<Property>();
            List<FieldGroup> groups = structuredContentType.getFieldGroups();
            for (FieldGroup group : groups) {
                List<FieldDefinition> definitions = group.getFieldDefinitions();
                for (FieldDefinition definition : definitions) {
                    Property property = new Property();
                    property.setName(definition.getName());
                    FieldMetadata fieldMetadata = new FieldMetadata();
                    property.setMetadata(fieldMetadata);
                    fieldMetadata.setFieldType(definition.getFieldType());
                    fieldMetadata.setMutable(true);
                    fieldMetadata.setInheritedFromType(StructuredContentTypeImpl.class.getName());
                    fieldMetadata.setAvailableToTypes(new String[] {StructuredContentTypeImpl.class.getName()});
                    fieldMetadata.setCollection(false);
                    fieldMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);
                    fieldMetadata.setLength(definition.getMaxLength());
                    if (definition.getFieldEnumeration() != null && !CollectionUtils.isEmpty(definition.getFieldEnumeration().getEnumerationItems())) {
                        int count = definition.getFieldEnumeration().getEnumerationItems().size();
                        String[][] enumItems = new String[count][2];
                        for (int j=0;j<count;j++) {
                            FieldEnumerationItem item = definition.getFieldEnumeration().getEnumerationItems().get(j);
                            enumItems[j][0] = item.getName();
                            enumItems[j][1] = item.getFriendlyName();
                        }
                        fieldMetadata.setEnumerationValues(enumItems);
                    }
                    FieldPresentationAttributes attributes = new FieldPresentationAttributes();
                    fieldMetadata.setPresentationAttributes(attributes);
                    attributes.setName(definition.getName());
                    attributes.setFriendlyName(definition.getFriendlyName());
                    attributes.setSecurityLevel(definition.getSecurityLevel()==null?"":definition.getSecurityLevel());
                    attributes.setOrder(fieldCount++);
                    attributes.setHidden(definition.getHiddenFlag());
                    attributes.setGroup(group.getName());
                    attributes.setGroupOrder(groupCount);
                    attributes.setGroupCollapsed(group.getInitCollapsedFlag());
                    attributes.setExplicitFieldType(SupportedFieldType.UNKNOWN);
                    attributes.setLargeEntry(definition.getTextAreaFlag());
                    attributes.setProminent(false);
                    attributes.setColumnWidth(String.valueOf(definition.getColumnWidth()));
                    attributes.setBroadleafEnumeration("");
                    attributes.setReadOnly(false);
                    if (definition.getValidationRegEx() != null) {
                        Map<String, String> itemMap = new HashMap<String, String>();
                        itemMap.put("regularExpression", definition.getValidationRegEx());
                        itemMap.put("errorMessageKey", definition.getValidationErrorMesageKey());
                        attributes.getValidationConfigurations().put("com.smartgwt.client.widgets.form.validator.RegExpValidator", itemMap);
                    }
                    propertiesList.add(property);
                }
                groupCount++;
                fieldCount = 0;
            }
            Property property = new Property();
            property.setName("id");
            FieldMetadata fieldMetadata = new FieldMetadata();
            property.setMetadata(fieldMetadata);
            fieldMetadata.setFieldType(SupportedFieldType.ID);
            fieldMetadata.setSecondaryType(SupportedFieldType.INTEGER);
            fieldMetadata.setMutable(true);
            fieldMetadata.setInheritedFromType(StructuredContentTypeImpl.class.getName());
            fieldMetadata.setAvailableToTypes(new String[] {StructuredContentTypeImpl.class.getName()});
            fieldMetadata.setCollection(false);
            fieldMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);
            FieldPresentationAttributes attributes = new FieldPresentationAttributes();
            fieldMetadata.setPresentationAttributes(attributes);
            attributes.setName("id");
            attributes.setFriendlyName("ID");
            attributes.setSecurityLevel("");
            attributes.setHidden(true);
            attributes.setExplicitFieldType(SupportedFieldType.UNKNOWN);
            attributes.setLargeEntry(false);
            attributes.setProminent(false);
            attributes.setColumnWidth("*");
            attributes.setBroadleafEnumeration("");
            attributes.setReadOnly(true);
            propertiesList.add(property);

            Property[] properties = new Property[propertiesList.size()];
		    properties = propertiesList.toArray(properties);
		    Arrays.sort(properties, new Comparator<Property>() {
                public int compare(Property o1, Property o2) {
                    /*
                         * First, compare properties based on order fields
                         */
                    if (o1.getMetadata().getPresentationAttributes().getOrder() != null && o2.getMetadata().getPresentationAttributes().getOrder() != null) {
                        return o1.getMetadata().getPresentationAttributes().getOrder().compareTo(o2.getMetadata().getPresentationAttributes().getOrder());
                    } else if (o1.getMetadata().getPresentationAttributes().getOrder() != null && o2.getMetadata().getPresentationAttributes().getOrder() == null) {
                        /*
                              * Always favor fields that have an order identified
                              */
                        return -1;
                    } else if (o1.getMetadata().getPresentationAttributes().getOrder() == null && o2.getMetadata().getPresentationAttributes().getOrder() != null) {
                        /*
                              * Always favor fields that have an order identified
                              */
                        return 1;
                    } else if (o1.getMetadata().getPresentationAttributes().getFriendlyName() != null && o2.getMetadata().getPresentationAttributes().getFriendlyName() != null) {
                        return o1.getMetadata().getPresentationAttributes().getFriendlyName().compareTo(o2.getMetadata().getPresentationAttributes().getFriendlyName());
                    } else {
                        return o1.getName().compareTo(o2.getName());
                    }
                }
            });
		    metadata.setProperties(properties);
            DynamicResultSet results = new DynamicResultSet(metadata);

            return results;
        } catch (Exception e) {
            LOG.error("Unable to execute persistence activity", e);
            throw new ServiceException("Unable to perform inspect for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }
    }

    @Override
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            String structuredContentId = persistencePackage.getCustomCriteria()[1];
            Entity entity = fetchEntityBasedOnId(structuredContentId);
            DynamicResultSet results = new DynamicResultSet(new Entity[]{entity}, 1);

            return results;
        } catch (Exception e) {
            LOG.error("Unable to execute persistence activity", e);
            throw new ServiceException("Unable to perform fetch for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }
    }

    protected Entity fetchEntityBasedOnId(String structuredContentId) throws Exception {
        StructuredContent structuredContent = (StructuredContent) structuredContentService.findStructuredContentById(Long.valueOf(structuredContentId));
        Map<String, StructuredContentField> structuredContentFieldMap = structuredContent.getStructuredContentFields();
        Entity entity = new Entity();
        entity.setType(new String[]{StructuredContentType.class.getName()});
        List<Property> propertiesList = new ArrayList<Property>();
        for (FieldGroup fieldGroup : structuredContent.getStructuredContentType().getFieldGroups()) {
            for (FieldDefinition definition : fieldGroup.getFieldDefinitions()) {
                Property property = new Property();
                propertiesList.add(property);
                property.setName(definition.getName());
                String value;
                checkValue: {
                    if (!MapUtils.isEmpty(structuredContentFieldMap)) {
                        StructuredContentField structuredContentField = structuredContentFieldMap.get(definition.getName());
                        List<StructuredContentFieldData> fieldDataList = structuredContentField.getFieldDataList();
                        if (!CollectionUtils.isEmpty(fieldDataList)) {
                            //TODO add support for multiple values
                            value = fieldDataList.get(0).getValue();
                            break checkValue;
                        }
                    }
                    value = null;
                }
                property.setValue(value);
            }
        }
        Property property = new Property();
        propertiesList.add(property);
        property.setName("id");
        property.setValue(structuredContentId);

        entity.setProperties(propertiesList.toArray(new Property[]{}));

        return entity;
    }

    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            String structuredContentId = persistencePackage.getCustomCriteria()[1];
            StructuredContent structuredContent = (StructuredContent) structuredContentService.findStructuredContentById(Long.valueOf(structuredContentId));
            //build up some fields before we detach page from the session
            List<String> templateFieldNames = new ArrayList<String>();
            for (FieldGroup group : structuredContent.getStructuredContentType().getFieldGroups()) {
                for (FieldDefinition definition: group.getFieldDefinitions()) {
                    templateFieldNames.add(definition.getName());
                }
            }
            for (String key : structuredContent.getStructuredContentFields().keySet()) {
                StructuredContentField structuredContentField = structuredContent.getStructuredContentFields().get(key);
                structuredContentField.getFieldDataList().size();
            }
            //detach page from the session so that our changes are not persisted here (we want to let the service take care of this)
            structuredContent = (StructuredContent) SerializationUtils.clone(structuredContent);
            Map<String, StructuredContentField> structuredContentFieldMap = structuredContent.getStructuredContentFields();
            for (Property property : persistencePackage.getEntity().getProperties()) {
                if (templateFieldNames.contains(property.getName())) {
                    StructuredContentField structuredContentField = structuredContentFieldMap.get(property.getName());
                    if (structuredContentField != null) {
                        if (!CollectionUtils.isEmpty(structuredContentField.getFieldDataList())) {
                            StructuredContentFieldData fieldData = structuredContentField.getFieldDataList().get(0);
                            fieldData.setValue(property.getValue());
                        } else {
                            StructuredContentFieldData fieldData = new StructuredContentFieldDataImpl();
                            fieldData.setValue(property.getValue());
                            //fileService.addFieldData(fieldData);
                            structuredContentField.getFieldDataList().add(fieldData);
                        }
                    } else {
                        structuredContentField = new StructuredContentFieldImpl();
                        structuredContentFieldMap.put(property.getName(), structuredContentField);
                        structuredContentField.setFieldKey(property.getName());
                        structuredContentField.setStructuredContent(structuredContent);
                        StructuredContentFieldData fieldData = new StructuredContentFieldDataImpl();
                        fieldData.setValue(property.getValue());
                        //fileService.addFieldData(fieldData);
                        structuredContentField.getFieldDataList().add(fieldData);
                    }
                }
            }
            List<String> removeItems = new ArrayList<String>();
            for (String key : structuredContentFieldMap.keySet()) {
                if (persistencePackage.getEntity().findProperty(key)==null) {
                    removeItems.add(key);
                }
            }
            if (removeItems.size() > 0) {
                for (String removeKey : removeItems) {
                    StructuredContentField structuredContentField = structuredContentFieldMap.remove(removeKey);
                    structuredContentField.setStructuredContent(null);
                }
            }
            structuredContentService.updateStructuredContent(structuredContent, getSandBox());

            return fetchEntityBasedOnId(structuredContentId);
        } catch (Exception e) {
            LOG.error("Unable to execute persistence activity", e);
            throw new ServiceException("Unable to perform fetch for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }
    }
}