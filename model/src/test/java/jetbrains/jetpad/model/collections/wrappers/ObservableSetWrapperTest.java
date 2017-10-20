/*
 * Copyright 2012-2017 JetBrains s.r.o
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.jetpad.model.collections.wrappers;

import jetbrains.jetpad.base.function.Function;
import jetbrains.jetpad.model.collections.CollectionItemEvent;
import jetbrains.jetpad.model.collections.CollectionListener;
import jetbrains.jetpad.model.collections.set.ObservableHashSet;
import jetbrains.jetpad.model.collections.set.ObservableSet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.Arrays;

import static jetbrains.jetpad.model.collections.ObservableItemEventMatchers.addEvent;
import static jetbrains.jetpad.model.collections.ObservableItemEventMatchers.removeEvent;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class ObservableSetWrapperTest {
  private ObservableSet<Double> source = new ObservableHashSet<>();
  private Function<Double, Integer> toTarget = new Function<Double, Integer>() {
    @Override
    public Integer apply(Double value) {
      return value.intValue() + 1;
    }
  };
  private Function<Integer, Double> toSource = new Function<Integer, Double>() {
    @Override
    public Double apply(Integer value) {
      return Integer.valueOf(value - 1).doubleValue();
    }
  };
  private ObservableSet<Integer> target = new ObservableSetWrapper<>(source, toTarget, toSource);

  private CollectionListener<Double> sourceListener = Mockito.mock(CollectionListener.class);
  private CollectionListener<Integer> targetListener = Mockito.mock(CollectionListener.class);
  private InOrder inOrder = Mockito.inOrder(sourceListener, targetListener);

  private CollectingListener listener = new CollectingListener();

  @Before
  public void setup() {
    source.addAll(Arrays.asList(10.0, 20.0, 30.0));
    target.addListener(listener);
  }

  @Test
  public void setMapMaps() {
    assertThat(target, containsInAnyOrder(11, 21, 31));
  }

  @Test
  public void setMapAddSource() {
    source.add(15.0);
    assertThat(target, containsInAnyOrder(11, 16, 21, 31));
    listener.assertEvents(1, 0);
    assertThat(listener.getAddEvents().get(0), is(addEvent(equalTo(16), equalTo(-1))));
  }

  @Test
  public void listMapAddTarget() {
    target.add(15);
    assertThat(target, containsInAnyOrder(11, 15, 21, 31));
    assertThat(source, containsInAnyOrder(10.0, 14.0, 20.0, 30.0));
    listener.assertEvents(1, 0);
    assertThat(listener.getAddEvents().get(0), is(addEvent(equalTo(15), equalTo(-1))));
  }

  @Test
  public void setMapRemoveSource() {
    source.remove(30.0);
    assertThat(target, containsInAnyOrder(11, 21));
    listener.assertEvents(0, 1);
    assertThat(listener.getRemoveEvents().get(0), is(removeEvent(equalTo(31), equalTo(-1))));
  }

  @Test
  public void setMapRemoveTarget() {
    target.remove(31);
    assertThat(target, containsInAnyOrder(11, 21));
    assertThat(source, containsInAnyOrder(10.0, 20.0));
    listener.assertEvents(0, 1);
    assertThat(listener.getRemoveEvents().get(0), is(removeEvent(equalTo(31), equalTo(-1))));
  }

  @Test
  public void setMapListenerSourceThenTargetOnSourceAdd() {
    source.addListener(sourceListener);
    target.addListener(targetListener);
    source.add(0.0);

    inOrder.verify(sourceListener)
        .onItemAdded(new CollectionItemEvent<>(null, 0.0, -1, CollectionItemEvent.EventType.ADD));
    inOrder.verify(targetListener)
        .onItemAdded(new CollectionItemEvent<>(null, 1, -1, CollectionItemEvent.EventType.ADD));
  }

  @Test
  public void setMapListenerTargetThenSourceOnSourceAdd() {
    target.addListener(targetListener);
    source.addListener(sourceListener);

    source.add(0.0);

    inOrder.verify(targetListener)
        .onItemAdded(new CollectionItemEvent<>(null, 1, -1, CollectionItemEvent.EventType.ADD));
    inOrder.verify(sourceListener)
        .onItemAdded(new CollectionItemEvent<>(null, 0.0, -1, CollectionItemEvent.EventType.ADD));
  }

  @Test
  public void setMapListenerSourceThenTargetOnTargetAdd() {
    source.addListener(sourceListener);
    target.addListener(targetListener);

    target.add(0);

    inOrder.verify(sourceListener)
        .onItemAdded(new CollectionItemEvent<>(null, -1.0, -1, CollectionItemEvent.EventType.ADD));
    inOrder.verify(targetListener)
        .onItemAdded(new CollectionItemEvent<>(null, 0, -1, CollectionItemEvent.EventType.ADD));
  }

  @Test
  public void setMapListenerTargetThenSourceOnTargetAdd() {
    target.addListener(targetListener);
    source.addListener(sourceListener);

    target.add(0);

    inOrder.verify(targetListener)
        .onItemAdded(new CollectionItemEvent<>(null, 0, -1, CollectionItemEvent.EventType.ADD));
    inOrder.verify(sourceListener)
        .onItemAdded(new CollectionItemEvent<>(null, -1.0, -1, CollectionItemEvent.EventType.ADD));
  }

}