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
package jetbrains.jetpad.mapper;

import jetbrains.jetpad.model.collections.set.ObservableSet;
import jetbrains.jetpad.test.BaseTestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class MapperTest extends BaseTestCase {
  private Item source = createItemTree();
  private ItemMapper mapper;
  private Item target;

  public MapperTest() {
  }

  @Before
  public void init() {
    mapper = new ItemMapper(source);
    mapper.attachRoot();
    target = mapper.getTarget();
  }

  @Test
  public void initialMapping() {
    assertMapped();
  }

  @Test
  public void propertyChange() {
    source.getName().set("xyz");

    assertMapped();
  }

  @Test
  public void removeItemFromObservable() {
    source.getObservableChildren().remove(0);

    assertMapped();
  }

  @Test
  public void addItemToObservable() {
    source.getObservableChildren().add(0, new Item());

    assertMapped();
  }

  @Test
  public void removeItemFromSimple() {
    source.getChildren().remove(0);
    mapper.refreshSimpleRole();

    assertMapped();
  }

  @Test
  public void addItemToSimple() {
    source.getChildren().add(new Item());
    mapper.refreshSimpleRole();

    assertMapped();
  }

  @Test
  public void singleChildSet() {
    source.getSingleChild().set(new Item());

    assertMapped();
  }

  @Test
  public void singleChildSetToNull() {
    source.getSingleChild().set(new Item());
    source.getSingleChild().set(null);

    assertMapped();
  }

  @Test
  public void rolesAreCleanedOnDetach() {
    assertFalse(target.getObservableChildren().isEmpty());
    assertFalse(target.getChildren().isEmpty());
    assertFalse(target.getTransformedChildren().isEmpty());

    mapper.detachRoot();

    assertTrue(target.getObservableChildren().isEmpty());
    assertTrue(target.getChildren().isEmpty());
    assertTrue(target.getTransformedChildren().isEmpty());
  }

  @Test
  public void illegalStateExceptionOnDetachBug() {
    TestMapper mapper = new TestMapper(new Object());
    mapper.attachRoot();

    mapper.attachChild();
    mapper.detachChild();
    mapper.attachChild();

    mapper.detachRoot();
  }

  @Test
  public void illegalStateExceptionOnDetachBugInCaseOfClearCall() {
    TestMapper mapper = new TestMapper(new Object());
    mapper.attachRoot();

    mapper.attachChild();
    mapper.detachChildren();
    mapper.attachChild();

    mapper.detachRoot();
  }

  @Test
  public void findableRoot() {
    Object o = new Object();
    TestMapper mapper = new TestMapper(o);
    mapper.attachRoot();
    assertSame(mapper, mapper.getDescendantMapper(o));

    mapper.detachRoot();
  }

  @Test
  public void nonFindableRoot() {
    Object o = new Object();
    TestMapper mapper = new TestMapper(o) {
      @Override
      protected boolean isFindable() {
        return false;
      }
    };
    mapper.attachRoot();
    assertNull(mapper.getDescendantMapper(o));

    mapper.detachChild();
  }

  @Test
  public void mappingContextListeners() {
    MappingContextListener l = Mockito.mock(MappingContextListener.class);

    MappingContext ctx = new MappingContext();
    ctx.addListener(l);

    TestMapper mapper = new TestMapper(new Object());
    mapper.attachRoot(ctx);
    mapper.detachRoot();

    Mockito.verify(l).onMapperRegistered(mapper);
    Mockito.verify(l).onMapperUnregistered(mapper);
  }

  private void assertMapped() {
    assertTrue(source.contentEquals(target));
  }

  private static Item createItemTree() {
    Item result = new Item();
    result.getName().set("xyz");

    for (int i = 0; i < 3; i++) {
      Item child = new Item();
      child.getName().set("child" + i);
      result.getObservableChildren().add(child);
      result.getChildren().add(child);
      result.getTransformedChildren().add(child);
    }

    return result;
  }

  private static class TestMapper extends Mapper<Object, Object> {
    private ObservableSet<Mapper<?,?>> children;
    private TestMapper child;

    TestMapper(Object source) {
      super(source, new Object());
      children = createChildSet();
    }

    void attachChild() {
      child = new TestMapper(new Object());
      children.add(child);
    }

    void detachChild() {
      children.remove(child);
      child = null;
    }

    void detachChildren() {
      children.clear();
      child = null;
    }
  }
}