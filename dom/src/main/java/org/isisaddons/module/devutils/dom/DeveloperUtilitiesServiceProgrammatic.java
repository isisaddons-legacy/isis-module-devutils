/*
 *  Copyright 2013~2014 Dan Haywood
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.isisaddons.module.devutils.dom;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.apache.isis.applib.FatalException;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.adapter.mgr.AdapterManager;
import org.apache.isis.core.metamodel.adapter.mgr.AdapterManagerAware;
import org.apache.isis.core.metamodel.layoutmetadata.json.LayoutMetadataReaderFromJson;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.spec.SpecificationLoaderSpi;
import org.apache.isis.core.metamodel.spec.SpecificationLoaderSpiAware;
import org.apache.isis.core.metamodel.spec.feature.Contributed;
import org.apache.isis.core.metamodel.spec.feature.ObjectAction;
import org.apache.isis.core.metamodel.spec.feature.ObjectAssociation;
import org.apache.isis.core.metamodel.spec.feature.OneToManyAssociation;
import org.apache.isis.core.metamodel.spec.feature.OneToOneAssociation;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class DeveloperUtilitiesServiceProgrammatic implements DeveloperUtilitiesService, SpecificationLoaderSpiAware, AdapterManagerAware {

    // //////////////////////////////////////

    private final MimeType mimeTypeTextCsv;
    private final MimeType mimeTypeApplicationZip;
    private final MimeType mimeTypeApplicationJson;

    public DeveloperUtilitiesServiceProgrammatic() {
        try {
            mimeTypeTextCsv = new MimeType("text", "csv");
            mimeTypeApplicationJson = new MimeType("application", "jzon");
            mimeTypeApplicationZip = new MimeType("application", "zip");
        } catch (final MimeTypeParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    // //////////////////////////////////////

    @Programmatic
    public Clob downloadMetaModel() {

        final Collection<ObjectSpecification> specifications = specificationLoader.allSpecifications();

        final List<MetaModelRow> rows = Lists.newArrayList();
        for (final ObjectSpecification spec : specifications) {
            if (exclude(spec)) {
                continue;
            }
            final List<ObjectAssociation> properties = spec.getAssociations(Contributed.EXCLUDED, ObjectAssociation.Filters.PROPERTIES);
            for (final ObjectAssociation property : properties) {
                final OneToOneAssociation otoa = (OneToOneAssociation) property;
                if (exclude(otoa)) {
                    continue;
                }
                rows.add(new MetaModelRow(spec, otoa));
            }
            final List<ObjectAssociation> associations = spec.getAssociations(Contributed.EXCLUDED, ObjectAssociation.Filters.COLLECTIONS);
            for (final ObjectAssociation collection : associations) {
                final OneToManyAssociation otma = (OneToManyAssociation) collection;
                if (exclude(otma)) {
                    continue;
                }
                rows.add(new MetaModelRow(spec, otma));
            }
            final List<ObjectAction> actions = spec.getObjectActions(Contributed.INCLUDED);
            for (final ObjectAction action : actions) {
                if (exclude(action)) {
                    continue;
                }
                rows.add(new MetaModelRow(spec, action));
            }
        }

        Collections.sort(rows);

        final StringBuilder buf = new StringBuilder();
        buf.append(MetaModelRow.header()).append("\n");
        for (final MetaModelRow row : rows) {
            buf.append(row.asTextCsv()).append("\n");
        }
        return new Clob("metamodel.csv", mimeTypeTextCsv, buf.toString().toCharArray());
    }

    protected boolean exclude(final OneToOneAssociation property) {
        return false;
    }

    protected boolean exclude(final OneToManyAssociation collection) {
        return false;
    }

    protected boolean exclude(final ObjectAction action) {
        return false;
    }

    protected boolean exclude(final ObjectSpecification spec) {
        return isBuiltIn(spec) || spec.isAbstract();
    }

    protected boolean isBuiltIn(final ObjectSpecification spec) {
        final String className = spec.getFullIdentifier();
        return className.startsWith("java") || className.startsWith("org.joda");
    }

    // //////////////////////////////////////

    @Programmatic
    public void refreshServices() {
        final Collection<ObjectSpecification> specifications = Lists.newArrayList(specificationLoader.allSpecifications());
        for (final ObjectSpecification objectSpec : specifications) {
            if(objectSpec.isService()){
                specificationLoader.invalidateCache(objectSpec.getCorrespondingClass());
            }
        }
    }

    // //////////////////////////////////////

    @Programmatic
    public Object refreshLayout(final Object domainObject) {
        specificationLoader.invalidateCacheFor(domainObject);
        return domainObject;
    }

    // //////////////////////////////////////

    public Clob downloadLayout(final Object domainObject) {

        final ObjectAdapter adapterFor = adapterManager.adapterFor(domainObject);
        final ObjectSpecification objectSpec = adapterFor.getSpecification();

        final LayoutMetadataReaderFromJson propertiesReader = new LayoutMetadataReaderFromJson();
        final String json = propertiesReader.asJson(objectSpec);

        return new Clob(objectSpec.getShortIdentifier() +".layout.json", mimeTypeApplicationJson, json);
    }

    // //////////////////////////////////////

    @Programmatic
    public Blob downloadLayouts() {
        final LayoutMetadataReaderFromJson propertiesReader = new LayoutMetadataReaderFromJson();
        final Collection<ObjectSpecification> allSpecs = specificationLoader.allSpecifications();
        final Collection<ObjectSpecification> domainObjectSpecs = Collections2.filter(allSpecs, new Predicate<ObjectSpecification>(){
            @Override
            public boolean apply(final ObjectSpecification input) {
                return  !input.isAbstract() && 
                        !input.isService() && 
                        !input.isValue() && 
                        !input.isParentedOrFreeCollection();
            }});
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final ZipOutputStream zos = new ZipOutputStream(baos);
            final OutputStreamWriter writer = new OutputStreamWriter(zos);
            for (final ObjectSpecification objectSpec : domainObjectSpecs) {
                zos.putNextEntry(new ZipEntry(zipEntryNameFor(objectSpec)));
                writer.write(propertiesReader.asJson(objectSpec));
                writer.flush();
                zos.closeEntry();
            }
            writer.close();
            return new Blob("layouts.zip", mimeTypeApplicationZip, baos.toByteArray());
        } catch (final IOException ex) {
            throw new FatalException("Unable to create zip of layouts", ex);
        }
    }

    private static String zipEntryNameFor(final ObjectSpecification objectSpec) {
        final String fqn = objectSpec.getFullIdentifier();
        return fqn.replace(".", File.separator)+".layout.json";
    }


    // //////////////////////////////////////

    private SpecificationLoaderSpi specificationLoader;

    @Programmatic
    @Override
    public void setSpecificationLoaderSpi(final SpecificationLoaderSpi specificationLoader) {
        this.specificationLoader = specificationLoader;
    }

    private AdapterManager adapterManager;

    @Programmatic
    @Override
    public void setAdapterManager(final AdapterManager adapterManager) {
        this.adapterManager = adapterManager;
    }


}
