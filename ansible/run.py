import subprocess
import sys
def subprocess_run(command):
    p = subprocess.Popen(command, shell=True, stdin=subprocess.PIPE,
                         stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    out, err = p.communicate(b"' stdin")
    cd = p.returncode
    return out

if __name__ == '__main__':

    """
    service host name
    check host
    """

    if(sys.argv[1] == "service"):
        host = sys.argv[2]
        name = sys.argv[3]

        out = subprocess_run("""ansible-playbook -i '%s,' 
        --private-key=/home/xxpasquxx/.ssh/ansible_rsa_key 
        /opt/monitoring/DevOpsMetricExposer/ansible/test-service-running-and-boot.yml 
        -e 'ansible_ssh_user=xxpasquxx' 
        -e 'host_key_checking=False' --extra-vars "service_pretty=%s service=%s" """ % (host, name, name))

        print(out)

    else:
        host = sys.argv[2]

        out = subprocess_run("""ansible -i '%s,' all 
        --private-key=$SSHKeyFile 
        -e 'ansible_ssh_user=$ansibleSSHUser'
         -e 'host_key_checking=False' 
         -m ping""" % (host))

         print(out)
