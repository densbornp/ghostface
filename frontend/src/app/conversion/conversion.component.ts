import { HttpHeaders, HttpParams } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ConversionService } from '../services/conversion.service';
import { BsModalService } from 'ngx-bootstrap/modal';
import { CookieModalComponent } from '../cookie-modal/cookie-modal.component';

@Component({
  selector: 'app-conversion',
  templateUrl: './conversion.component.html',
  styleUrls: ['./conversion.component.scss']
})
export class ConversionComponent implements OnInit {

  choosenFile: string;
  infoText: string;
  INFO_FINISHED: "Finished!";
  INFO_DOWNLOAD: "Download successful!";
  INFO_UPLOAD: "Upload successful!";
  downloadBtnDisabled = true;
  private config = {
    animated: true,
    ignoreBackdropClick: true,
    'class': 'modal-dialog-centered'
  };

  constructor(private conversionService: ConversionService, private modalService: BsModalService) { }

  ngOnInit(): void {
      this.choosenFile = "Choose file";
      this.infoText = null;
  }

  public uploadImage(event: any) {
    let uploadedFile = event.srcElement.files[0];
    this.choosenFile = uploadedFile.name;
    let formData = new FormData();
    formData.append("imageFile", uploadedFile);
    // TODO subscribe gets not triggered
    this.conversionService.uploadFile(formData).subscribe(() => {
        console.log("IN");
        this.downloadBtnDisabled = false;
        this.infoText = this.INFO_UPLOAD;
    }, (error: any) => {
        console.log("IN_2", error);
        if (error.status === 422) {
            this.modalService.show(CookieModalComponent, this.config);
        } else {
            alert("Upload failed: " + error.error);
        }
        this.infoText = null;
    });
  }

  public downloadImage(event: any) {
    this.conversionService.downloadFile().subscribe((data: any) => {}, (error: any) => {
        alert("Download ended with an error: " + error.error);
        this.infoText = null;
    }, () => {this.infoText = this.INFO_DOWNLOAD;});
  }
}
