/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.isisaddons.module.devutils.dom.layoutjson;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.isisaddons.module.devutils.dom.layoutjson.repr.ActionLayoutRepr;
import org.isisaddons.module.devutils.dom.layoutjson.repr.ActionRepr;
import org.isisaddons.module.devutils.dom.layoutjson.repr.CollectionLayoutRepr;
import org.isisaddons.module.devutils.dom.layoutjson.repr.ColumnRepr;
import org.isisaddons.module.devutils.dom.layoutjson.repr.MemberGroupRepr;
import org.isisaddons.module.devutils.dom.layoutjson.repr.MemberRepr;
import org.isisaddons.module.devutils.dom.layoutjson.repr.PropertyLayoutRepr;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.LabelPosition;
import org.apache.isis.applib.annotation.MemberGroupLayout.ColumnSpans;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.filter.Filter;
import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facets.actions.notcontributed.NotContributedFacet;
import org.apache.isis.core.metamodel.facets.actions.position.ActionPositionFacet;
import org.apache.isis.core.metamodel.facets.all.describedas.DescribedAsFacet;
import org.apache.isis.core.metamodel.facets.all.hide.HiddenFacet;
import org.apache.isis.core.metamodel.facets.all.named.NamedFacet;
import org.apache.isis.core.metamodel.facets.collections.sortedby.SortedByFacet;
import org.apache.isis.core.metamodel.facets.members.cssclass.CssClassFacet;
import org.apache.isis.core.metamodel.facets.members.cssclassfa.CssClassFaFacet;
import org.apache.isis.core.metamodel.facets.members.render.RenderFacet;
import org.apache.isis.core.metamodel.facets.object.bookmarkpolicy.BookmarkPolicyFacet;
import org.apache.isis.core.metamodel.facets.object.membergroups.MemberGroupLayoutFacet;
import org.apache.isis.core.metamodel.facets.object.paged.PagedFacet;
import org.apache.isis.core.metamodel.facets.objectvalue.labelat.LabelAtFacet;
import org.apache.isis.core.metamodel.facets.objectvalue.multiline.MultiLineFacet;
import org.apache.isis.core.metamodel.facets.objectvalue.typicallen.TypicalLengthFacet;
import org.apache.isis.core.metamodel.spec.ActionType;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.spec.ObjectSpecifications;
import org.apache.isis.core.metamodel.spec.ObjectSpecifications.MemberGroupLayoutHint;
import org.apache.isis.core.metamodel.spec.feature.Contributed;
import org.apache.isis.core.metamodel.spec.feature.ObjectAction;
import org.apache.isis.core.metamodel.spec.feature.ObjectAssociation;
import org.apache.isis.core.metamodel.spec.feature.OneToManyAssociation;
import org.apache.isis.core.metamodel.spec.feature.OneToOneAssociation;

public class LayoutJsonExporter {

    static class LayoutMetadata {
        private List<ColumnRepr> columns;

        private Map<String, ActionRepr> actions;

        public List<ColumnRepr> getColumns() {
            return columns;
        }
        public void setColumns(List<ColumnRepr> columns) {
            this.columns = columns;

        }
        public Map<String, ActionRepr> getActions() {
            return actions;
        }
        public void setActions(Map<String, ActionRepr> actions) {
            this.actions = actions;
        }
    }
    /**
     * not API
     */
    public String asJson(final ObjectSpecification objectSpec) {
        final LayoutMetadata metadata = new LayoutMetadata();
        metadata.setColumns(Lists.<ColumnRepr>newArrayList());
        
        final MemberGroupLayoutFacet mglf = objectSpec.getFacet(MemberGroupLayoutFacet.class);
        final ColumnSpans columnSpans = mglf.getColumnSpans();
        
        final Set<String> actionIdsForAssociations = Sets.newTreeSet();
        
        ColumnRepr columnRepr;
        
        columnRepr = addColumnWithSpan(metadata, columnSpans.getLeft());
        updateColumnMemberGroups(objectSpec, MemberGroupLayoutHint.LEFT, columnRepr);
        
        columnRepr = addColumnWithSpan(metadata, columnSpans.getMiddle());
        updateColumnMemberGroups(objectSpec, MemberGroupLayoutHint.MIDDLE, columnRepr);
        
        columnRepr = addColumnWithSpan(metadata, columnSpans.getRight());
        updateColumnMemberGroups(objectSpec, MemberGroupLayoutHint.RIGHT, columnRepr);
        
        columnRepr = addColumnWithSpan(metadata, columnSpans.getCollections());
        updateCollectionColumnRepr(objectSpec, columnRepr);

        addActions(objectSpec, metadata, actionIdsForAssociations);
        
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(metadata);
    }

    private static void updateColumnMemberGroups(final ObjectSpecification objectSpec, final MemberGroupLayoutHint hint, final ColumnRepr columnRepr) {
        final List<ObjectAssociation> objectAssociations = propertiesOf(objectSpec);
        final Map<String, List<ObjectAssociation>> associationsByGroup = ObjectAssociation.Util.groupByMemberOrderName(objectAssociations);
        
        final List<String> groupNames = ObjectSpecifications.orderByMemberGroups(objectSpec, associationsByGroup.keySet(), hint);
        
        columnRepr.memberGroups = Maps.newLinkedHashMap();
        for (final String groupName : groupNames) {
            final MemberGroupRepr memberGroupRepr = new MemberGroupRepr();
            columnRepr.memberGroups.put(groupName, memberGroupRepr);
            final List<ObjectAssociation> associationsInGroup = associationsByGroup.get(groupName);
            memberGroupRepr.members = Maps.newLinkedHashMap();
            if(associationsInGroup == null) {
                continue;
            }
            for (final ObjectAssociation assoc : associationsInGroup) {
                final MemberRepr memberRepr = newMemberRepr(assoc);
                memberGroupRepr.members.put(assoc.getId(), memberRepr);
            }
        }
    }

    private static void updateCollectionColumnRepr(final ObjectSpecification objectSpec, final ColumnRepr columnRepr) {
        final List<ObjectAssociation> objectAssociations = collectionsOf(objectSpec);
        columnRepr.collections = Maps.newLinkedHashMap();
        for(final ObjectAssociation assoc: objectAssociations) {
            final MemberRepr memberRepr = newMemberRepr(assoc);
            columnRepr.collections.put(assoc.getId(), memberRepr);
        }
    }


    private static void addActions(final ObjectSpecification objectSpec, final LayoutMetadata metadata, final Set<String> actionIdsForAssociations) {
        final Map<String, ActionRepr> actions = Maps.newLinkedHashMap();
        final List<ObjectAction> actionsOf = actionsOf(objectSpec, actionIdsForAssociations);
        for(final ObjectAction action: actionsOf) {
            actions.put(action.getId(), newActionRepr(action));
        }
        metadata.setActions(actions);
    }

    private static MemberRepr newMemberRepr(final ObjectAssociation objectMember) {

        final MemberRepr memberRepr = new MemberRepr();

        if(objectMember instanceof OneToOneAssociation) {

            final PropertyLayoutRepr layoutRepr = new PropertyLayoutRepr();
            memberRepr.propertyLayout = layoutRepr;

            final CssClassFacet cssClassFacet = objectMember.getFacet(CssClassFacet.class);
            if(defined(cssClassFacet)) {
                layoutRepr.cssClass = Strings.emptyToNull(cssClassFacet.cssClass(null));
            }

            final DescribedAsFacet describedAsFacet = objectMember.getFacet(DescribedAsFacet.class);
            if(defined(describedAsFacet)) {
                layoutRepr.describedAs = Strings.emptyToNull(describedAsFacet.value());
            }

            final HiddenFacet hiddenFacet = objectMember.getFacet(HiddenFacet.class);
            if(defined(hiddenFacet)) {
                layoutRepr.hidden = whereNowhereToNull(hiddenFacet.where());
            }

            final LabelAtFacet labelAtFacet = objectMember.getFacet(LabelAtFacet.class);
            if(defined(labelAtFacet)) {
                layoutRepr.labelPosition = labelPositionLeftToNull(labelAtFacet.label());
            }

            final MultiLineFacet multiLineFacet = objectMember.getFacet(MultiLineFacet.class);
            if(defined(multiLineFacet)) {
                layoutRepr.multiLine = nonPositiveToNull(multiLineFacet.numberOfLines());
            }

            final NamedFacet namedFacet = objectMember.getFacet(NamedFacet.class);
            if(defined(namedFacet)) {
                layoutRepr.named = Strings.emptyToNull(namedFacet.value());
                layoutRepr.namedEscaped = trueToNull(namedFacet.escaped());
            }

            final TypicalLengthFacet typicalLengthFacet = objectMember.getFacet(TypicalLengthFacet.class);
            if(defined(typicalLengthFacet)) {
                layoutRepr.typicalLength = nonPositiveToNull(typicalLengthFacet.value());
            }
        }
        // else
        if(objectMember instanceof OneToManyAssociation) {
            final CollectionLayoutRepr layoutRepr = new CollectionLayoutRepr();
            memberRepr.collectionLayout = layoutRepr;

            final CssClassFacet cssClassFacet = objectMember.getFacet(CssClassFacet.class);
            if(defined(cssClassFacet)) {
                layoutRepr.cssClass = Strings.emptyToNull(cssClassFacet.cssClass(null));
            }

            final DescribedAsFacet describedAsFacet = objectMember.getFacet(DescribedAsFacet.class);
            if(defined(describedAsFacet)) {
                layoutRepr.describedAs = Strings.emptyToNull(describedAsFacet.value());
            }

            final HiddenFacet hiddenFacet = objectMember.getFacet(HiddenFacet.class);
            if(defined(hiddenFacet)) {
                layoutRepr.hidden = whereNowhereToNull(hiddenFacet.where());
            }

            final NamedFacet namedFacet = objectMember.getFacet(NamedFacet.class);
            if(defined(namedFacet)) {
                layoutRepr.named = Strings.emptyToNull(namedFacet.value());
                layoutRepr.namedEscaped = trueToNull(namedFacet.escaped());
            }

            final PagedFacet pagedFacet = objectMember.getFacet(PagedFacet.class);
            if(defined(pagedFacet)) {
                layoutRepr.paged = nonPositiveToNull(pagedFacet.value());
            }

            final RenderFacet renderFacet = objectMember.getFacet(RenderFacet.class);
            if(defined(renderFacet)) {
                layoutRepr.render = renderLazilyToNull(renderFacet.value());
            }

            final SortedByFacet sortedByFacet = objectMember.getFacet(SortedByFacet.class);
            if(defined(sortedByFacet)) {
                layoutRepr.sortedBy = nameOfElseNull(sortedByFacet.value());
            }
        }
        return memberRepr;
    }

    private static ActionRepr newActionRepr(final ObjectAction objectMember) {

        final ActionRepr memberRepr = new ActionRepr();
        final ActionLayoutRepr layoutRepr = new ActionLayoutRepr();
        memberRepr.actionLayout = layoutRepr;

        final BookmarkPolicyFacet bookmarkPolicyFacet = objectMember.getFacet(BookmarkPolicyFacet.class);
        if(defined(bookmarkPolicyFacet)) {
            layoutRepr.bookmarking = bookmarkPolicyNeverToNull(bookmarkPolicyFacet.value());
        }

        final CssClassFacet cssClassFacet = objectMember.getFacet(CssClassFacet.class);
        if(defined(cssClassFacet)) {
            layoutRepr.cssClass = Strings.emptyToNull(cssClassFacet.cssClass(null));
        }

        final CssClassFaFacet cssClassFaFacet = objectMember.getFacet(CssClassFaFacet.class);
        if(defined(cssClassFaFacet)) {
            layoutRepr.cssClassFa = Strings.emptyToNull(cssClassFaFacet.value());
            layoutRepr.cssClassFaPosition = cssClassFaPositionLeftToNull(cssClassFaFacet.getPosition());
        }

        final DescribedAsFacet describedAsFacet = objectMember.getFacet(DescribedAsFacet.class);
        if(defined(describedAsFacet)) {
            layoutRepr.describedAs = Strings.emptyToNull(describedAsFacet.value());
        }

        final HiddenFacet hiddenFacet = objectMember.getFacet(HiddenFacet.class);
        if(defined(hiddenFacet)) {
            layoutRepr.hidden = whereNowhereToNull(hiddenFacet.where());
        }

        final NamedFacet namedFacet = objectMember.getFacet(NamedFacet.class);
        if(defined(namedFacet)) {
            layoutRepr.named = Strings.emptyToNull(namedFacet.value());
        }

        final ActionPositionFacet positionFacet = objectMember.getFacet(ActionPositionFacet.class);
        if(defined(positionFacet)) {
            layoutRepr.position = actionPositionBelowToNull(positionFacet.position());
        }

        final NotContributedFacet notContributedFacet = objectMember.getFacet(NotContributedFacet.class);
        if(defined(notContributedFacet)) {
            layoutRepr.contributed = convert(notContributedFacet.value());
        }

        return memberRepr;
    }

    private static boolean defined(final Facet facet) {
        return facet != null && !facet.isNoop();
    }

    private static ColumnRepr addColumnWithSpan(final LayoutMetadata metadata, final int span) {
        final ColumnRepr col = new ColumnRepr();
        metadata.getColumns().add(col);
        col.span = span;
        return col;
    }

    private static List<ObjectAssociation> propertiesOf(final ObjectSpecification objSpec) {
        return objSpec.getAssociations(Contributed.EXCLUDED, ObjectAssociation.Filters.PROPERTIES);
    }
    private static List<ObjectAssociation> collectionsOf(final ObjectSpecification objSpec) {
        return objSpec.getAssociations(Contributed.EXCLUDED, ObjectAssociation.Filters.COLLECTIONS);
    }
    private static List<ObjectAction> actionsOf(final ObjectSpecification objSpec, final Set<String> excludedActionIds) {
        return objSpec.getObjectActions(ActionType.ALL, Contributed.INCLUDED, excluding(excludedActionIds));
    }

    @SuppressWarnings({ "deprecation" })
    private static Filter<ObjectAction> excluding(final Set<String> excludedActionIds) {
        return new Filter<ObjectAction>(){
                    @Override
                    public boolean accept(final ObjectAction t) {
                        return !excludedActionIds.contains(t.getId());
                    }
                };
    }


    private static BookmarkPolicy bookmarkPolicyNeverToNull(final BookmarkPolicy value) {
        return value == BookmarkPolicy.NEVER? null: value;
    }

    public static ActionLayout.CssClassFaPosition cssClassFaPositionLeftToNull(final ActionLayout.CssClassFaPosition position) {
        return position == ActionLayout.CssClassFaPosition.LEFT? null: position;
    }

    private static Boolean trueToNull(final boolean escaped) {
        return escaped? null: Boolean.FALSE;
    }

    private static String nameOfElseNull(final Class<? extends Comparator<?>> cls) {
        return cls != null? cls.getName(): null;
    }

    private static RenderType renderLazilyToNull(final Render.Type value) {
        switch (value) {
            case LAZILY:
                return null;
            case EAGERLY:
                return RenderType.EAGERLY;
            default:
                // shouldn't happen, above enumerates all values
                return null;
        }
    }

    private static Integer nonPositiveToNull(final int i) {
        return i<=0? null: i;
    }

    public static LabelPosition labelPositionLeftToNull(final LabelPosition position) {
        return position == LabelPosition.LEFT? null: position;
    }

    private static ActionLayout.Position actionPositionBelowToNull(final ActionLayout.Position position) {
        return position == ActionLayout.Position.BELOW? null: position;
    }

    private static Where whereNowhereToNull(final Where where) {
        return where == Where.NOWHERE ? null : where;
    }

    private static org.apache.isis.applib.annotation.Contributed convert(
            final NotContributed.As value) {
        switch (value) {
            case ACTION:
                return org.apache.isis.applib.annotation.Contributed.AS_ASSOCIATION;
            case ASSOCIATION:
                return org.apache.isis.applib.annotation.Contributed.AS_ACTION;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return getClass().getName();
    }

}
