/*
 * Copyright (C) 2020 Grakn Labs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package grakn.core.graql.planning.gremlin.sets;

import com.google.common.collect.ImmutableSet;
import grakn.core.graql.planning.gremlin.fragment.InHasFragment;
import grakn.core.graql.planning.gremlin.fragment.InKeyFragment;
import grakn.core.graql.planning.gremlin.fragment.OutHasFragment;
import grakn.core.graql.planning.gremlin.fragment.OutKeyFragment;
import grakn.core.kb.graql.planning.gremlin.Fragment;
import graql.lang.property.VarProperty;
import graql.lang.statement.Variable;

import java.util.Set;

public class KeyFragmentSet extends EquivalentFragmentSetImpl {

    private final Variable ownerTypeVar;
    private final Variable attributeTypeVar;

    public KeyFragmentSet(VarProperty property, Variable ownerTypeVar, Variable attributeTypeVar) {
        super(property);
        this.ownerTypeVar = ownerTypeVar;
        this.attributeTypeVar = attributeTypeVar;
    }

    @Override
    public Set<Fragment> fragments() {
        return ImmutableSet.of(
                new InKeyFragment(varProperty(), attributeTypeVar, ownerTypeVar),
                new OutKeyFragment(varProperty(), ownerTypeVar, attributeTypeVar));
    }
}
