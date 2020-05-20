import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from "@angular/forms";
import { UploadService } from  'src/app/services/upload.service';
import { Property } from 'src/app/common/property';

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.css']
})

export class FileUploadComponent implements OnInit {
 
  imageURL: string;
  uploadForm: FormGroup;
  properties: Property[];
  public userFile: any = File;

  constructor(public fb: FormBuilder, private _uploadService: UploadService) {
    // Reactive Form
    this.uploadForm = this.fb.group({
      avatar: [null],
      name: ['']
    })
  }

  ngOnInit(): void { }


  // Image Preview
  showPreview(event) {
    const file = (event.target as HTMLInputElement).files[0];
    this.userFile = file;
    this.uploadForm.patchValue({
      avatar: file
    });
    this.uploadForm.get('avatar').updateValueAndValidity()

    // File Preview
    const reader = new FileReader();
    reader.onload = () => {
      this.imageURL = reader.result as string;
    }
    reader.readAsDataURL(file)
  }

  // Submit Form
  submit() {
    console.log(this.uploadForm.value)
    const formData = new FormData();
    formData.append('file', (this.userFile));
    this.listPropertiesFrom(formData);
  }


  listPropertiesFrom(formData: FormData) {
    this._uploadService.upload(formData).subscribe(data => {
        console.log(data);  
        this.properties = data;
    })
  }

}