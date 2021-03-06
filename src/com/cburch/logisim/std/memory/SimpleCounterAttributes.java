/*******************************************************************************
 * This file is part of logisim-evolution.
 *
 *   logisim-evolution is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   logisim-evolution is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with logisim-evolution.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Original code by Carl Burch (http://www.cburch.com), 2011.
 *   Subsequent modifications by :
 *     + Haute École Spécialisée Bernoise
 *       http://www.bfh.ch
 *     + Haute École du paysage, d'ingénierie et d'architecture de Genève
 *       http://hepia.hesge.ch/
 *     + Haute École d'Ingénierie et de Gestion du Canton de Vaud
 *       http://www.heig-vd.ch/
 *   The project is currently maintained by :
 *     + REDS Institute - HEIG-VD
 *       Yverdon-les-Bains, Switzerland
 *       http://reds.heig-vd.ch
 *******************************************************************************/

package com.cburch.logisim.std.memory;

import java.util.List;

import com.cburch.logisim.data.AbstractAttributeSet;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.AttributeSets;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.instance.StdAttr;

class SimpleCounterAttributes extends AbstractAttributeSet {

	private AttributeSet base;

	public SimpleCounterAttributes() {
		base = AttributeSets.fixedSet(new Attribute<?>[] { StdAttr.WIDTH,
				SimpleCounter.ATTR_MAX, SimpleCounter.ATTR_ON_GOAL, StdAttr.EDGE_TRIGGER,
				StdAttr.LABEL, StdAttr.LABEL_FONT },
				new Object[] { BitWidth.create(8), Integer.valueOf(0xFF),
						SimpleCounter.ON_GOAL_WRAP, StdAttr.TRIG_RISING, "",
						StdAttr.DEFAULT_LABEL_FONT });
	}

	@Override
	public boolean containsAttribute(Attribute<?> attr) {
		return base.containsAttribute(attr);
	}

	@Override
	public void copyInto(AbstractAttributeSet dest) {
		((SimpleCounterAttributes) dest).base = (AttributeSet) this.base.clone();
	}

	@Override
	public Attribute<?> getAttribute(String name) {
		return base.getAttribute(name);
	}

	@Override
	public List<Attribute<?>> getAttributes() {
		return base.getAttributes();
	}

	@Override
	public <V> V getValue(Attribute<V> attr) {
		return base.getValue(attr);
	}

	@Override
	public boolean isReadOnly(Attribute<?> attr) {
		return base.isReadOnly(attr);
	}

	@Override
	public void setReadOnly(Attribute<?> attr, boolean value) {
		base.setReadOnly(attr, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V> void setValue(Attribute<V> attr, V value) {
		Object oldValue = base.getValue(attr);
		if (oldValue == null ? value == null : oldValue.equals(value))
			return;

		Integer newMax = null;
		if (attr == StdAttr.WIDTH) {
			BitWidth oldWidth = base.getValue(StdAttr.WIDTH);
			BitWidth newWidth = (BitWidth) value;
			int oldW = oldWidth.getWidth();
			int newW = newWidth.getWidth();
			Integer oldValObj = base.getValue(SimpleCounter.ATTR_MAX);
			int oldVal = oldValObj.intValue();
			base.setValue(StdAttr.WIDTH, newWidth);
			if (newW > oldW) {
				newMax = Integer.valueOf(newWidth.getMask());
			} else {
				int v = oldVal & newWidth.getMask();
				if (v != oldVal) {
					Integer newValObj = Integer.valueOf(v);
					base.setValue(SimpleCounter.ATTR_MAX, newValObj);
					fireAttributeValueChanged(SimpleCounter.ATTR_MAX, newValObj,null);
				}
			}
			fireAttributeValueChanged(StdAttr.WIDTH, newWidth,null);
		} else if (attr == SimpleCounter.ATTR_MAX) {
			int oldVal = base.getValue(SimpleCounter.ATTR_MAX).intValue();
			BitWidth width = base.getValue(StdAttr.WIDTH);
			int newVal = ((Integer) value).intValue() & width.getMask();
			if (newVal != oldVal) {
				V val = (V) Integer.valueOf(newVal);
				value = val;
			}
		}
		base.setValue(attr, value);
		fireAttributeValueChanged(attr, value,(V)oldValue);
		if (newMax != null) {
			base.setValue(SimpleCounter.ATTR_MAX, newMax);
			fireAttributeValueChanged(SimpleCounter.ATTR_MAX, newMax,null);
		}
	}
}
