packer {
  required_plugins {
    amazon = {
      source  = "github.com/hashicorp/amazon"
      version = ">= 1.0.0"
    }
  }
}

variable "aws_region" {
  type    = string
  default = "us-east-1"
}

variable "source_ami" {
  type    = string
  default = "ami-06db4d78cb1d3bbf9" # Debian-12
}

variable "ssh_username" {
  type    = string
  default = "admin"
}

variable "subnet_id" {
  type    = string
  default = "subnet-0f100f76292a200be"
}

variable "profile" {
  type    = string
  default = "dev"
}
variable "instance_type" {
  type    = string
  default = "t2.micro"
}

source "amazon-ebs" "my-ami" {
  region          = "${var.aws_region}"
  ami_name        = "Cloud_${formatdate("YYYY_MM_DD_hh_mm_ss", timestamp())}"
  ami_description = "AMI for CSYE 6225 (Cloud Computing)"
  profile         = "${var.profile}"
  instance_type   = "${var.instance_type}"
  source_ami      = "${var.source_ami}"
  ssh_username    = "${var.ssh_username}"
  ami_users       = ["889683117020"]
}


build {
  sources = ["source.amazon-ebs.my-ami"]
  provisioner "shell" {
    environment_vars = [
      "DEBIAN_FRONTEND=noninteractive",
      "CHECKPOINT_DISABLE=1"
    ]
    script = "myscript.sh"
  }
  provisioner "file" {
    source      = "../target/Assignment3-0.0.1-SNAPSHOT.jar"
    destination = "/tmp/Assignment3-0.0.1-SNAPSHOT.jar"
  }
  provisioner "file" {
    source      = "../opt/users.csv"
    destination = "/tmp/users.csv"
  }
  provisioner "file" {
    source      = "cloud.service"
    destination = "/tmp/cloud.service"
  }
  provisioner "file" {
    source      = "application.properties"
    destination = "/tmp/application.properties"
  }
  provisioner "file" {
    source      = "cloudwatch-config.json"
    destination = "/tmp/cloudwatch-config.json"
  }
  provisioner "shell" {
    inline = [
      "sudo groupadd csye6225",
      "sudo useradd -s /bin/false -g csye6225 -d /opt/webapp -m csye6225",
      "sudo mv /tmp/cloud.service /etc/systemd/system/cloud.service",
      "sudo mv /tmp/cloudwatch-config.json /opt/cloudwatch-config.json",
      "sudo mv /tmp/users.csv /opt/users.csv",
      "sudo cp /tmp/application.properties /opt/application.properties",
      "sudo mv /tmp/Assignment3-0.0.1-SNAPSHOT.jar /opt/webapp/Assignment3-0.0.1-SNAPSHOT.jar",
      "sudo chown csye6225:csye6225 /opt/webapp/Assignment3-0.0.1-SNAPSHOT.jar",
      "sudo systemctl daemon-reload",
      "sudo systemctl enable cloud",
      "sudo wget https://amazoncloudwatch-agent.s3.amazonaws.com/debian/amd64/latest/amazon-cloudwatch-agent.deb",
      "sudo dpkg -i -E ./amazon-cloudwatch-agent.deb"
    ]
  }
}

