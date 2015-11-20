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

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Clob;

import org.isisaddons.module.devutils.DevUtilsModule;

@DomainService(
        nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY
)
@DomainServiceLayout(
        named = "Prototyping",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY
)
public class DeveloperUtilitiesServiceContributions  {

    public static abstract class PropertyDomainEvent<T> extends DevUtilsModule.PropertyDomainEvent<DeveloperUtilitiesServiceContributions, T> {
    }

    public static abstract class CollectionDomainEvent<T> extends DevUtilsModule.CollectionDomainEvent<DeveloperUtilitiesServiceContributions, T> {
    }

    public static abstract class ActionDomainEvent extends DevUtilsModule.ActionDomainEvent<DeveloperUtilitiesServiceContributions> {
    }


    // //////////////////////////////////////

    public static class DownloadLayoutEvent extends ActionDomainEvent {}

    /**
     * Download the JSON layout of the domain object's type.
     */
    @Action(
            domainEvent = DownloadLayoutEvent.class,
            semantics = SemanticsOf.SAFE,
            restrictTo = RestrictTo.PROTOTYPING
    )
    @ActionLayout(
            contributed = Contributed.AS_ACTION,
            cssClassFa = "fa-download"
    )
    @MemberOrder(sequence="500.900")
    public Clob downloadLayout(final Object domainObject) {
        return developerUtilitiesService.downloadLayout(domainObject);
    }

    // //////////////////////////////////////

    @Inject
    private DeveloperUtilitiesService developerUtilitiesService;
}
