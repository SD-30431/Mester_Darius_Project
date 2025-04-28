import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageRobotsComponent } from './manage-robots.component';

describe('ManageRobotsComponent', () => {
  let component: ManageRobotsComponent;
  let fixture: ComponentFixture<ManageRobotsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManageRobotsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManageRobotsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
